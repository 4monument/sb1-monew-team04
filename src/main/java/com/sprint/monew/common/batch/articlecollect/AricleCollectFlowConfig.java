package com.sprint.monew.common.batch.articlecollect;

import static com.sprint.monew.common.batch.util.CustomExecutionContextKeys.NAVER_ARTICLE_DTOS;

import com.sprint.monew.common.batch.util.ArticlesAndArticleInterestsDTO;
import com.sprint.monew.common.batch.util.ExecutionContextFinder;
import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.api.ArticleApiClient;
import com.sprint.monew.domain.article.api.ArticleApiDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
@RequiredArgsConstructor
public class AricleCollectFlowConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final ArticleApiClient articleApiClient;

  // Naver Flow -> API를 호출 + 처리
  @Bean(name = "naverArticleCollectFlow")
  @JobScope
  public Flow naverArticleCollectFlow(
      @Qualifier("naverApiCallTasklet") Tasklet naverApiCallTasklet,
      @Qualifier("articleHandlerStep") Step articleHandlerStep) {
    // 호출
    TaskletStep naverArticleCollectStep = new StepBuilder("naverArticleCollectStep", jobRepository)
        .tasklet(naverApiCallTasklet, transactionManager)
        .build();

    return new FlowBuilder<Flow>("naverCollectFlow")
        .start(naverArticleCollectStep)
        .next(articleHandlerStep) // 처리  스텝
        .build();
  }

  @Bean
  @StepScope
  public Tasklet naverApiCallTasklet() {
    return (contribution, chunkContext) -> {
      // 네이버 호출하는 로직있다고 가정
      List<ArticleApiDto> articleApiDtos = articleApiClient.getNaverArticle();

      // API로 호출한 뉴스를 배치 저장소에 저장
      ExecutionContext jobExecutionContext = ExecutionContextFinder.findJobExecutionContext(
          contribution);
      jobExecutionContext.put(NAVER_ARTICLE_DTOS.getKey(), articleApiDtos);
      return RepeatStatus.FINISHED;
    };
  }

  @Bean
  @JobScope
  public Step articleHandlerStep(
      @Qualifier("naverArticleCollectReader") ItemReader<ArticleApiDto> naverArticleCollectReader,
      @Qualifier("naverArticleCollectProcessor") ItemProcessor<ArticleApiDto, Article> naverArticleCollectProcessor,
      @Qualifier("articleJpaItemWriter") ItemWriter<Article> articleCollectJpaItemWriter) {

    return new StepBuilder("articleHandlerStep", jobRepository)
        .<ArticleApiDto, Article>chunk(30, transactionManager)
        .reader(naverArticleCollectReader)
        .processor(naverArticleCollectProcessor)
        .writer(articleCollectJpaItemWriter)
        .faultTolerant()
        .retryLimit(3)
        .retry(Exception.class)
        .build();
  }


  // Chosun Flow




  // Hankyung Flow
}

