package com.sprint.monew.common.batch.config;

import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.CHOSUN_ARTICLE_DTOS;
import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.HANKYUNG_ARTICLE_DTOS;
import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.NAVER_ARTICLE_DTOS;

import com.sprint.monew.common.batch.support.InterestContainer;
import com.sprint.monew.domain.article.api.ArticleApiClient;
import com.sprint.monew.domain.article.api.ArticleApiDto;
import com.sprint.monew.domain.article.api.chosun.ChosunArticleClient.ChosunCategory;
import com.sprint.monew.domain.article.api.hankyung.HankyungArticleClient.HankyungCategory;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ArticleApiCallTaskletConfig {

  private final ArticleApiClient articleApiClient;
  private final InterestContainer interestContainer;

  @Bean
  @StepScope
  public Tasklet naverApiCallTasklet() {
    return (contribution, chunkContext) -> {
      List<ArticleApiDto> articleApiDtos = articleApiClient.getNaverArticle();

      log.info("가져온 naver 기사 수 : {}", articleApiDtos.size());
      List<ArticleApiDto> filteredApiDtos = filteringArticles(articleApiDtos);
      log.info("필터링 후 naver 기사 수 : {}", filteredApiDtos.size());

      ExecutionContext stepContext = contribution.getStepExecution().getExecutionContext();
      stepContext.put(NAVER_ARTICLE_DTOS.getKey(), filteredApiDtos);
      return RepeatStatus.FINISHED;
    };
  }

  @Bean
  @StepScope
  public Tasklet chosunApiCallTasklet() {
    return (contribution, chunkContext) -> {
      List<ArticleApiDto> articleApiDtos = articleApiClient.getChosunArticle(ChosunCategory.ALL);
      List<ArticleApiDto> filteredApiDtos = filteringArticles(articleApiDtos);

      ExecutionContext stepContext = contribution.getStepExecution().getExecutionContext();
      stepContext.put(CHOSUN_ARTICLE_DTOS.getKey(), filteredApiDtos);
      return RepeatStatus.FINISHED;
    };
  }

  @Bean
  @StepScope
  public Tasklet hankyungApiCallTasklet() {
    return (contribution, chunkContext) -> {
      List<ArticleApiDto> articleApiDtos = articleApiClient.getHankyungArticle(HankyungCategory.ALL);
      List<ArticleApiDto> filteredApiDtos = filteringArticles(articleApiDtos);

      ExecutionContext stepContext = contribution.getStepExecution().getExecutionContext();
      stepContext.put(HANKYUNG_ARTICLE_DTOS.getKey(), filteredApiDtos);
      return RepeatStatus.FINISHED;
    };
  }

  private List<ArticleApiDto> filteringArticles(List<ArticleApiDto> articleApiDtos) {
    return articleApiDtos.stream()
        .map(interestContainer::filter)
        .filter(Objects::nonNull)
        .toList();
  }
}
