package com.sprint.monew.common.batch.articlecollect;

import com.sprint.monew.common.batch.support.ArticleWithInterestList;
import com.sprint.monew.common.batch.support.Interests;
import com.sprint.monew.domain.article.api.ArticleApiDto;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArticleProcessorConfig {

  @Bean
  @StepScope
  public ItemProcessor<ArticleApiDto, ArticleWithInterestList> basicArticleCollectProcessor(
      @Value("#{JobExecutionContext['interests']}") Interests interests) {
    return (dto) -> {
      // 중복 되는 url은 패스
      if (interests.isDuplicateUrl(dto)) {
        return null; // null 반환하면 배치에서는 Writer에 skilp
      }
      return interests.toArticleWithRelevantInterests(dto);
    };
  }
}
