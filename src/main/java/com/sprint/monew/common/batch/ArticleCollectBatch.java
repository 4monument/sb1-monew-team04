package com.sprint.monew.common.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ArticleCollectBatch {

  // Chunk 프로세싱으로

  @Primary
  @Bean
  public Job articleCollectJob() {
    return null;
  }

  @Bean
  public Step articleCollectStep() {
    return null;
  }

  // ItemReader

  // ItemProcessor

  // ItemWriter
}
