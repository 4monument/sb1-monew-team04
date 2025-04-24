package com.sprint.monew.common.batch.articlecollect;

import com.sprint.monew.common.batch.util.Keywords;
import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.api.ArticleApiDto;
import java.util.List;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArticleProcessorConfig {

  @Bean
  @StepScope
  public ItemProcessor<Object, Article> naverArticleCollectProcessor(
      @Value("#{JobExecutionContext['naverArticleDtos']}") List<ArticleApiDto> articleApiDtos,
      @Value("#{JobExecutionContext['keywords']}") Keywords keywords) {

    return (item) -> {
      if (keywords == null) {
        // 변환 및 다른 도메인 생성 로직 넣기
        return null;
      }

      List<ArticleApiDto> filteredArticleDtos = keywords.filter(articleApiDtos);
      // 변환로직
      //
      return null;
    };
  }

  @Bean
  @StepScope
  public ItemProcessor<Object, Article> chosunArticleCollectProcessor(
      @Value("#{JobExecutionContext['chosunArticleDtos']}") List<ArticleApiDto> articleApiDtos,
      @Value("#{JobExecutionContext['keywords']}") Keywords keywords) {

    return (item) -> {
      if (keywords == null) {
        // 변환 로직 넣기
        return null;
      }

      List<ArticleApiDto> filteredArticleDtos = keywords.filter(articleApiDtos);

      // 변환로직
      return null;
    };
  }


}
