package com.sprint.monew.common.batch;

import com.sprint.monew.domain.article.Article;
import io.awspring.cloud.s3.S3Resource;
import jakarta.annotation.Resource;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class S3BackupBatch {

  private final EntityManagerFactory emf;
  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;

  @Resource(name = "articleS3Resource")
  private final S3Resource articleS3Resource;

  @Bean(name = "s3BackupJob")
  public Job s3BackupJob() {
    return new JobBuilder("s3BackupJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .start(s3BackupStep())
        .build();
  }

  @Bean
  public Step s3BackupStep() {
    return new StepBuilder("s3BackupStep", jobRepository)
        .<Article, Article>chunk(2, transactionManager)
        .reader(jpaPagingItemReader())
        .writer(flatFileItemWriter())
        .build();
  }

  // ItemReader : jpaPagingItemReader 기본 구현체 사용
  @Bean
  public JpaPagingItemReader<Article> jpaPagingItemReader(){
    String query = "SELECT a FROM Article a";
    return new JpaPagingItemReaderBuilder<Article>()
        .name("articleJpaPagingItemReader")
        .pageSize(10)
        .entityManagerFactory(emf)
        .queryString(query)
        .build();
  }

  // ItemWriter : S3에 백업하기 위한 FlatFileItemWriter 구현
  @Bean
  public FlatFileItemWriter<Article> flatFileItemWriter() {
    // 문제는
    return new FlatFileItemWriterBuilder<Article>()
        .name("s3BackupWriter")
        .resource(articleS3Resource)
        .append(true)
        .delimited().delimiter("|")  // 나중에 @Value로 바꾸기
        .names(getFieldNames())
        .build();
  }

  // 하드코딩 해결 나중에 생각하기
  private String[] getFieldNames() {
    return new String[]{
        "id",
        "source",
        "sourceUrl",
        "title",
        "pulishDate",
        "summary",
        "deleted"
    };
  }
}
