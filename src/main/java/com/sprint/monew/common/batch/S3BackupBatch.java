package com.sprint.monew.common.batch;

import com.sprint.monew.domain.article.Article;
import com.sprint.monew.global.config.S3ConfigProperties;
import io.awspring.cloud.autoconfigure.s3.properties.S3Properties;
import io.awspring.cloud.s3.S3OutputStreamProvider;
import io.awspring.cloud.s3.S3Resource;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManagerFactory;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class S3BackupBatch {

  private final EntityManagerFactory emf;
  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final S3ConfigProperties s3Properties;
  private final S3Client s3Client;
  private S3Resource articleS3Resource; // 매일 변하는 변수(백업 하는 날마다 PATH가 달라짐)

  @Bean("s3BackupJob")
  public Job s3BackupJob(@Qualifier("s3BackupStep") Step s3BackupStep) {
    return new JobBuilder("s3BackupJob", jobRepository)
        .start(s3BackupStep)
        .build();
  }

  @Bean("s3BackupStep")
  @JobScope
  public Step s3BackupStep(
      @Qualifier("s3BackupJpaPagingItemReader") JpaPagingItemReader<Article> jpaPagingItemReader,
      @Qualifier("s3BackupCustomItemWriter") ItemWriter<Article> s3BackupCustomItemWriter,
      @Value("#{jobParameters['dateTime']}") LocalDateTime runDateTime,
      S3OutputStreamProvider s3OutputStreamProvider) {

    articleS3Resource = createS3Resource(runDateTime, s3OutputStreamProvider);
    log.info("S3 Resource Created : {}", articleS3Resource.getLocation());

    return new StepBuilder("s3BackupStep", jobRepository)
        .<Article, Article>chunk(2, transactionManager)
        .reader(jpaPagingItemReader)
        .writer(s3BackupCustomItemWriter)
        .build();
  }

  // ItemReader : jpaPagingItemReader 기본 구현체 사용
  // 오늘 날짜의 기사만 백업
  @Bean
  @StepScope
  public JpaPagingItemReader<Article> s3BackupJpaPagingItemReader(
      @Value("#{jobParameters['dateTime']}") LocalDateTime runDateParam) {

    Instant startOfRunDate = getStartOfRunDate(runDateParam);
    String publishDateParam = "startOfToday";
    // 임시 쿼리 : 나중에 시간나면 QueryDSL로 바꿀 것
    String tempQuery = String.format("SELECT a FROM Article a WHERE a.publishDate >= :%s",
        publishDateParam);

    return new JpaPagingItemReaderBuilder<Article>()
        .name("articleJpaPagingItemReader")
        .pageSize(10) // 나중에 pageSize 조절하고 동적으로 받을지 결정
        .entityManagerFactory(emf)
        .queryString(tempQuery)
        .parameterValues(Map.of(publishDateParam, startOfRunDate)) // 나중에 QueryDSL로
        .build();
  }

  @Bean
  public ItemWriter<Article> s3BackupCustomItemWriter() {
    return items -> {
      try (BufferedOutputStream writer =
          new BufferedOutputStream(articleS3Resource.getOutputStream())) {
        log.info("S3 Backup Writer Run");
        log.info("s3b");
        for (Article item : items) {
          writer.write(String.format("%s|%s|%s|%s|%s|%s|%s\n",
              item.getId(),
              item.getSource(),
              item.getSourceUrl(),
              item.getTitle(),
              item.getPublishDate(),
              item.getSummary(),
              item.isDeleted()).getBytes());
        }
      }
    };
  }

  private Instant getStartOfRunDate(LocalDateTime runDateParam) {
    return runDateParam.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant();
  }

  private S3Resource createS3Resource(LocalDateTime runDateTime,
      S3OutputStreamProvider s3OutputStreamProvider) {
    String fileName = runDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv";
    String location = "s3://" + s3Properties.bucket() + "/" + fileName;
    return S3Resource.create(location, s3Client, s3OutputStreamProvider);
  }

  //  @Bean
//  public FlatFileItemWriter<Article> flatFileItemWriter() {
//    // 문제는
////    String location = "s3://" + s3Properties.bucket() + "/";
//    OutputStream outputStream = null;
//    try {
//      outputStream = articleS3Resource.getOutputStream();
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//    return new FlatFileItemWriterBuilder<Article>()
//        .name("s3BackupWriter")
//        .resource((WritableResource) outputStream)
//        .append(true)
//        .delimited().delimiter("|")  // 나중에 @Value로 바꾸기
//        .names(getFieldNames())
//        .build();
//  }

//  // 하드코딩 해결 나중에 생각하기
//  private String[] getFieldNames() {
//    return new String[]{
//        "id",
//        "source",
//        "sourceUrl",
//        "title",
//        "pulishDate",
//        "summary",
//        "deleted"
//    };
//  }
}
