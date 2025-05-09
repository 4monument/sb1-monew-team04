package com.sprint.monew.common.batch.config;

import com.sprint.monew.domain.article.api.ArticleApiDto;
import com.sprint.monew.global.config.S3ConfigProperties;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ArticleBackupConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final S3Client s3Client;
  private final S3ConfigProperties s3Properties;

  /**
   * 임시로 로컬에 저장
   */
  @Bean
  public Step localBackupArticlesStep(
      @Qualifier("backupContextReader") ItemReader<ArticleApiDto> backupArticlesContextReader,
      @Qualifier("backupLocalArticlesWriter") FlatFileItemWriter<ArticleApiDto> backupLocalArticlesWriter) {
    return new StepBuilder("backupArticlesStep", jobRepository)
        .<ArticleApiDto, ArticleApiDto>chunk(200, transactionManager)
        .reader(backupArticlesContextReader)
        .writer(backupLocalArticlesWriter)
        .build();
  }

  @Bean
  @StepScope
  public ItemReader<ArticleApiDto> backupContextReader(
      @Value("#{jobExecutionContext['naverArticleDtos']}") List<ArticleApiDto> naver,
      @Value("#{jobExecutionContext['chosunArticleDtos']}") List<ArticleApiDto> chosun,
      @Value("#{jobExecutionContext['hankyungArticleDtos']}") List<ArticleApiDto> hankyung) {
    // 신문사 추가할떄마다 추가할 곳
    List<ArticleApiDto> allDtos = new ArrayList<>();
    if (naver != null) {
      allDtos.addAll(naver);
    }

    if (chosun != null) {
      allDtos.addAll(chosun);
    }

    if (hankyung != null) {
      allDtos.addAll(hankyung);
    }

    log.info("backup read start : dto size = {}", allDtos.size());
    return new ListItemReader<>(allDtos);
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<ArticleApiDto> backupLocalArticlesWriter() {
    log.info("S3 Backup Writer Run");
    String[] header = {"source", "sourceUrl", "title", "publishDate", "summary"};

    return new FlatFileItemWriterBuilder<ArticleApiDto>()
        .name("backupLocalArticlesWriter")
        .append(false)
        .encoding("UTF-8")
        .shouldDeleteIfExists(true)
        .resource(new FileSystemResource(getNowLocalPath()))
        .delimited()                          // , 로 구분
        .delimiter(",")
        .quoteCharacter("\"")
        .names(header)
        .headerCallback(w ->
            w.write("\uFEFFsource,sourceUrl,title,publishDate,summary"))
        .build();
  }

  /**
   * 로컬에 저장한 파일을 S3에
   */
  @Bean
  @JobScope
  public Step uploadS3ArticleDtosStep() {
    return new StepBuilder("uploadS3ArticleDtosStep", jobRepository)
        .tasklet((contribution, chunkContext) -> {
          LocalDateTime now = LocalDateTime.now();
          String s3Path = String.format("%s/%s.csv", now.toLocalDate(), now.getHour());
          PutObjectRequest putObjectRequest = PutObjectRequest.builder()
              .key(s3Path)
              .bucket(s3Properties.bucket())
              .contentType("text/csv; charset=UTF-8")
              .build();

          File localCsvFile = new File(getNowLocalPath());
          RequestBody requestBody = RequestBody.fromFile(localCsvFile);

          s3Client.putObject(putObjectRequest, requestBody);

          Files.delete(localCsvFile.toPath());
          return RepeatStatus.FINISHED;

        }, transactionManager)
        .build();
  }

  /**
   * 편의 메서드
   */
  private String getNowLocalPath() {
    LocalDateTime now = LocalDateTime.now();
    return String.format("/tmp/%s-%s.csv", now.toLocalDate(), now.getHour());
  }
}
