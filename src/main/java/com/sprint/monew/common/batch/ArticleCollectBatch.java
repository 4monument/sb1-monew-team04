package com.sprint.monew.common.batch;

import com.sprint.monew.domain.article.Article;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ArticleCollectBatch {

  // Chunk 프로세싱으로

  @Autowired
  private EntityManagerFactory emf;

  @Autowired
  private PlatformTransactionManager transactionManager;

  @Autowired
  private JobRepository jobRepository;

  @Primary
  @Bean
  public Job articleCollectJob() {
    return null;
  }

  @Bean
  public Step articleCollectStep() {
    return null;
  }

  // 안 쓸 step : QueryDsl로 커스텀한 Step과 비교용으로 만든 간단한 step
  @Bean
  public Step articleStepByJpaItemWriter() {
     return new StepBuilder("articleStepByJpaItemWriter", jobRepository)
         .<Article, Article>chunk(10, transactionManager)
         .reader(jpaPagingItemReader()) // 나중에 구현
         .writer(articleJpaItemWriter()) //
         .build();
  }



  // ItemReader

  // ItemProcessor

  // ItemWriter


  // 성능 안좋은 Writer : JpaItemWriter
  @Bean
  public ItemWriter<Article> articleJpaItemWriter() {
     return new JpaItemWriterBuilder<Article>()
         .usePersist(true)
         .entityManagerFactory(emf)
         .build();
  }
}
