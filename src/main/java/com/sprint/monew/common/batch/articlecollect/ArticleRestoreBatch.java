package com.sprint.monew.common.batch.articlecollect;

import static org.springframework.web.servlet.function.RequestPredicates.contentType;

import com.sprint.monew.common.batch.support.ArticleWithInterestList;
import com.sprint.monew.domain.article.Article;
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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Qualifier;
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


  @Bean
  public Step articleRestoreStep(
      @Qualifier("articleRestoreItemReader") MultiResourceItemReader<ArticleApiDto> mrir
  ) {
    return new StepBuilder("articleRestoreStep", jobRepository)
        .<ArticleApiDto, ArticleWithInterestList>chunk(500, transactionManager)
        .reader(mrir)
        //.tasklet(articleRestoreTasklet(), transactionManager)
        .build();
  }


  // 일단 s3
  @Bean
  public MultiResourceItemReader<ArticleApiDto> articleRestoreItemReader(
      @Value("#{jobParameters['from']}") LocalDate from,
      @Value("#{jobParameters['to']}") LocalDate to) throws IOException {
    // 날짜
    List<LocalDate> dateRange = from.datesUntil(to).toList();

    List<S3Object> s3Objects = new ArrayList<>();
    for (LocalDate localDate : dateRange) {
      ListObjectsV2Request lovr = ListObjectsV2Request.builder()
          .bucket(s3Properties.bucket())
          .prefix(localDate + "/")
          .build();

      ListObjectsV2Response response = s3Client.listObjectsV2(lovr);
      s3Objects.addAll(response.contents());
    }

    List<Resource> resources = new ArrayList<>();
    for (S3Object s3Object : s3Objects) {

      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(s3Properties.bucket())
          .key(s3Object.key())
          .build();

      ResponseInputStream<GetObjectResponse> ri = s3Client.getObject(getObjectRequest);
      resources.add(new InputStreamResource(ri));
    }

    Resource[] resourcesArray = resources.toArray(Resource[]::new);
    return new MultiResourceItemReaderBuilder<ArticleApiDto>()
        .resources(resourcesArray)
        .delegate(csvReader())
        .build();
  }

  @Bean
  public ResourceAwareItemReaderItemStream<ArticleApiDto> csvReader() {
    String[] fieldNames = new String[]{"source", "sourceUrl", "title", "publishDate", "summary"};
    return new FlatFileItemReaderBuilder<ArticleApiDto>()
        .delimited()
        .delimiter(",")
        .names(fieldNames)
        .fieldSetMapper(new BeanWrapperFieldSetMapper<>(){
          {
            setTargetType(ArticleApiDto.class);
          }
        })
        .build();
  }
//
//  @Bean
//  public ItemProcessor<ArticleApiDto, ArticleWithInterestList> articleRestoreProcessor() {
//    return item -> {
//
//      Article article = Article.builder()
//          .source(Source.valueOf(item.getSource()))
//          .sourceUrl(item.getSourceUrl())
//          .title(item.getTitle())
//          .publishDate(Instant.parse(item.getPublishDate()))
//          .summary(item.getSummary())
//          .build();
//
//      return new ArticleWithInterestList(article, null);
//    };
//  }
//


  public String getKey(LocalDate localDate) {
    return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv";
  }
}
