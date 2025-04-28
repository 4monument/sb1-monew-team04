package com.sprint.monew.common.batch.articlecollect;

import static org.springframework.web.servlet.function.RequestPredicates.contentType;

import com.sprint.monew.domain.article.Article.Source;
import com.sprint.monew.domain.article.api.ArticleApiDto;
import com.sprint.monew.global.config.S3ConfigProperties;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.transaction.PlatformTransactionManager;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

@Configuration
@RequiredArgsConstructor
public class ArticleRestoreBatch {


  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final S3Client s3Client;
  private final S3ConfigProperties s3Properties;

  // 일단 s3
  @Bean
  public Tasklet articleRestoreTasklet(
      @Value("#{jobParameters['from']}") LocalDate from,
      @Value("#{jobParameters['to']}") LocalDate to) throws IOException {
    // 날짜
    List<LocalDate> dateRange  = from.datesUntil(to).toList();

    for (LocalDate localDate : dateRange) {
      ListObjectsV2Request lovr = ListObjectsV2Request.builder()
          .bucket(s3Properties.bucket())
          .prefix(localDate + "/")
          .build();

      ListObjectsV2Response response = s3Client.listObjectsV2(lovr);

      List<S3Object> contents = response.contents();

    }


    LocalDate tempDate = LocalDate.now();
    String fileName = tempDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv";

    List<ArticleApiDto> restoringArticleApiDtos = new ArrayList<>();
    for (LocalDate localDate : dateRange) {
      String key = getKey(localDate);

      GetObjectRequest objectRequest = GetObjectRequest.builder()
          .key(key)
          .responseContentType("text/csv")
          .bucket(s3Properties.bucket())
          .build();

      ResponseInputStream<GetObjectResponse> inputStream = s3Client.getObject(objectRequest);

      List<ArticleApiDto> dtos = new ArrayList<>();
      try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))){
        dtos = br.lines()
            .map(line -> {
              String[] cols = line.split(",");
              Source source = Source.valueOf(cols[0]);
              String sourceUrl = cols[1];
              String title = cols[2];
              Instant publishDate = Instant.parse(cols[3]);
              String summary = cols[4];
              return new ArticleApiDto(
                  source,
                  sourceUrl,
                  title,
                  summary,
                  publishDate
              );
            }).toList();
      }
      restoringArticleApiDtos.addAll(dtos);
    }

    return null;
  }

  public String getKey(LocalDate localDate) {
    return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv";
  }
}
