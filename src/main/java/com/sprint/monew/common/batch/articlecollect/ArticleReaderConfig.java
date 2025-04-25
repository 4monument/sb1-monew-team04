package com.sprint.monew.common.batch.articlecollect;

import com.sprint.monew.domain.article.api.ArticleApiDto;
import java.util.List;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArticleReaderConfig {

  @Bean(name = "naverArticleCollectReader")
  @StepScope
  public ItemReader<ArticleApiDto> naverArticleCollectReader(
      @Value("#{JobExecutionContext['naverArticleDtos']}") List<ArticleApiDto> naverArticleDtos) {
    return new ListItemReader<>(naverArticleDtos);
  }

  @Bean(name = "chosunArticleCollectReader")
  @StepScope
  public ItemReader<Object> chosunArticleCollectReader() {
    return null;
  }
}
