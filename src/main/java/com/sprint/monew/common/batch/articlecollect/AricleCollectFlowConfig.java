package com.sprint.monew.common.batch.articlecollect;

import static com.sprint.monew.common.batch.util.CustomExecutionContextKeys.NAVER_ARTICLE_DTOS;

import com.sprint.monew.common.batch.util.ArticleWithInterestList;
import com.sprint.monew.common.batch.util.ExecutionContextFinder;
import com.sprint.monew.domain.article.api.ArticleApiClient;
import com.sprint.monew.domain.article.api.ArticleApiDto;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
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
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
      //@Qualifier("basicArticleCollectProcessor") ItemProcessor<ArticleApiDto, ArticleWithInterestList> naverArticleCollectProcessor,
      //@Qualifier("articleJpaItemWriter") ItemWriter<ArticleWithInterestList> articleCollectJpaItemWriter
      @Qualifier("asyncItemProcessor") AsyncItemProcessor<ArticleApiDto, ArticleWithInterestList> asyncItemProcessor,
      @Qualifier("asyncItemWriter") AsyncItemWriter<ArticleWithInterestList> asyncItemWriter) {

    return new StepBuilder("articleHandlerStep", jobRepository)
        .<ArticleApiDto, Future<ArticleWithInterestList>>chunk(30, transactionManager)
        .reader(naverArticleCollectReader)
        .processor(asyncItemProcessor)
        .writer(asyncItemWriter)
        .faultTolerant()
        .retryLimit(3)
        .retry(Exception.class)
        .build();
  }

  @Bean(name = "asyncItemProcessor")
  @StepScope
  public AsyncItemProcessor<ArticleApiDto, ArticleWithInterestList> asyncItemProcessor(
      @Qualifier("basicArticleCollectProcessor") ItemProcessor<ArticleApiDto, ArticleWithInterestList> basicArticleCollectProcessor,
      @Qualifier("articleCollectThreadPoolTaskExecutor") ThreadPoolTaskExecutor articleCollectThreadPoolTaskExecutor)
      throws Exception {
    AsyncItemProcessor<ArticleApiDto, ArticleWithInterestList> asyncItemProcessor = new AsyncItemProcessor<>();
    asyncItemProcessor.setDelegate(basicArticleCollectProcessor);
    asyncItemProcessor.setTaskExecutor(articleCollectThreadPoolTaskExecutor);
    asyncItemProcessor.afterPropertiesSet();
    return asyncItemProcessor;
  }

  @Bean
  @StepScope
  public ThreadPoolTaskExecutor articleCollectThreadPoolTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(20);
    executor.setThreadNamePrefix("article-collect-processor-async-");
    executor.initialize();
    return executor;
  }

  @Bean
  @StepScope
  public AsyncItemWriter<ArticleWithInterestList> asyncItemWriter(
      @Qualifier("articleJpaItemWriter") ItemWriter<ArticleWithInterestList> articleCollectJpaItemWriter)
      throws Exception {
    AsyncItemWriter<ArticleWithInterestList> asyncItemWriter = new AsyncItemWriter<>();
    asyncItemWriter.setDelegate(articleCollectJpaItemWriter);
    asyncItemWriter.afterPropertiesSet();
    return asyncItemWriter;
  }


  // Chosun Flow

  // Hankyung Flow
}

