package com.sprint.monew.common.batch.articlecollect;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArticleReaderConfig {

  @Bean(name = "naverArticleCollectReader")
  @StepScope
  public ItemReader<Object> naverArticleCollectReader() {
    return null;
  }

  @Bean(name = "chosunArticleCollectReader")
  @StepScope
  public ItemReader<Object> chosunArticleCollectReader() {
    return null;
  }
}
