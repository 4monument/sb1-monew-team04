package com.sprint.monew.common.batch;

import com.sprint.monew.domain.article.Article;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class S3BackupBatch {

  private final EntityManagerFactory emf;
  private final JobRepository jobRepository;

  @Bean
  public Job s3BackupJob() {
    return null;
  }


  @Bean
  public Step s3BackupStep() {

    new StepBuilder("s3BackupStep", jobRepository)
        .<Article, Article>chunk(10)
        .reader(jpaPagingItemReader())
        .writer(null)
        .build();
  }


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



  private S3BackupItemWriter s3BackupItemWriter() {

    return new S3BackupItemWriter();
  }
}
