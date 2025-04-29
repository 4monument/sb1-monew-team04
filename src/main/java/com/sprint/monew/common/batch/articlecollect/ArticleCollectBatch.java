package com.sprint.monew.common.batch.articlecollect;

import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.*;
import static org.springframework.batch.core.ExitStatus.*;

import com.sprint.monew.common.batch.support.ArticleWithInterestList;
import com.sprint.monew.common.batch.support.Interests;
import com.sprint.monew.domain.article.api.ArticleApiDto;
import com.sprint.monew.domain.interest.InterestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
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
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ArticleCollectBatch {

  private final PlatformTransactionManager transactionManager;
  private final JobRepository jobRepository;

  @Bean(name = "articleCollectJob")
  public Job articleCollectJob(
      @Qualifier("interestsFetchStep") Step interestsFetchStep,
      @Qualifier("naverArticleCollectFlow") Flow naverArticleCollectFlow,
      @Qualifier("articleCollectJobContextCleanupListener") JobExecutionListener jobContextCleanupListener,
      @Qualifier("backupArticleJobStep") Step backupArticleJobStep) {

    return new JobBuilder("articleCollectJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .start(interestsFetchStep)
        .on(COMPLETED.getExitCode())
        .to(naverArticleCollectFlow)// 1. Article 자료 수집
        //.split(taskExecutor()).add(null) // 나중
        .next(backupArticleJobStep) // 2. backup + 필터링
        .end()
        .listener(jobContextCleanupListener)
        .build();
  }

  @Bean(name = "interestsFetchStep")
  @JobScope
  public Step interestsFetchStep(InterestRepository interestRepository,
      @Qualifier("interestsFetchPromotionListener") ExecutionContextPromotionListener promotionListener) {

    return new StepBuilder("interestsFetchStep", jobRepository)
        .tasklet((contribution, chunkContext) -> {

          ExecutionContext stepContext = contribution.getStepExecution().getExecutionContext();
          Interests interests = new Interests(interestRepository.findAll());
          stepContext.put(INTERESTS.getKey(), interests);

          return RepeatStatus.FINISHED;
        }, transactionManager)
        .listener(promotionListener)
        .build();
  }


  /**
   * Article API Collect Flow
   */
  @Bean(name = "naverArticleCollectFlow")
  @JobScope
  public Flow naverArticleCollectFlow(
      @Qualifier("naverApiCallTasklet") Tasklet naverApiCallTasklet,
      @Qualifier("naverArticleHandlerStep") Step articleHandlerStep,
      @Qualifier("naverPromotionListener") ExecutionContextPromotionListener promotionListener) {

    // 호출
    TaskletStep naverArticleCollectStep = new StepBuilder("naverArticleCollectStep", jobRepository)
        .tasklet(naverApiCallTasklet, transactionManager)
        .listener(promotionListener)
        .build();

    return new FlowBuilder<Flow>("naverCollectFlow")
        .start(naverArticleCollectStep)
        .next(articleHandlerStep) // 처리  스텝
        .build();
  }


  @Bean
  @JobScope
  public Step naverArticleHandlerStep(
      @Qualifier("naverArticleCollectReader") ItemReader<ArticleApiDto> naverArticleCollectReader,
      @Qualifier("basicArticleCollectProcessor") ItemProcessor<ArticleApiDto, ArticleWithInterestList> naverArticleCollectProcessor,
      @Qualifier("articleJpaItemWriter") ItemWriter<ArticleWithInterestList> articleJpaItemWriter,
      @Qualifier("naverExecutionContextCleanupListener") StepExecutionListener naverExecutionContextCleanupListener) {

    return new StepBuilder("articleHandlerStep", jobRepository)
        .<ArticleApiDto, ArticleWithInterestList>chunk(200, transactionManager)
        .reader(naverArticleCollectReader)
        .processor(naverArticleCollectProcessor)
        .writer(articleJpaItemWriter)
        .faultTolerant()
        .retryLimit(3)
        .retry(Exception.class)
        .listener(naverExecutionContextCleanupListener)
        .build();
  }

  /**
   * API Multi-threading TaskExecutor
   */
  @Bean
  public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setThreadNamePrefix("article-async-thread-");
    return executor;
  }
}
