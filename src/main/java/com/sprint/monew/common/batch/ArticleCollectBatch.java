package com.sprint.monew.common.batch;

import static org.springframework.batch.core.ExitStatus.COMPLETED;

import com.sprint.monew.common.batch.support.ArticleWithInterestList;
import com.sprint.monew.common.batch.support.InterestContainer;
import com.sprint.monew.domain.article.api.ArticleApiDto;
import com.sprint.monew.domain.article.repository.ArticleRepository;
import com.sprint.monew.domain.interest.Interest;
import com.sprint.monew.domain.interest.repository.InterestRepository;
import java.util.List;
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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ArticleCollectBatch {

  private final PlatformTransactionManager transactionManager;
  private final JobRepository jobRepository;

  @Primary
  @Bean(name = "articleCollectJob")
  public Job articleCollectJob(
      @Qualifier("interestsAndUrlsFetchStep") Step interestsAndUrlsFetchStep,
      @Qualifier("collecctArticlesSplitFlow") Flow collecctArticlesSplitFlow,
      @Qualifier("localBackupArticlesStep") Step localBackupStep,
      @Qualifier("uploadS3ArticleDtosStep") Step s3BackupStep,
      @Qualifier("articleHandlerStep") Step articleHandlerStep,
      @Qualifier("interestContainerCleanupListener") JobExecutionListener interestContainerCleanupListener) {

    return new JobBuilder("articleCollectJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .start(interestsAndUrlsFetchStep)
        .on(COMPLETED.getExitCode())
        .to(collecctArticlesSplitFlow)
        .next(localBackupStep)
        .next(s3BackupStep)
        .next(articleHandlerStep)
        .end()
        .listener(interestContainerCleanupListener)
        .build();
  }

  @Bean
  public Flow collecctArticlesSplitFlow(
      @Qualifier("naverArticleCollectFlow") Flow naverArticleCollectFlow,
      @Qualifier("chosunArticleCollectFlow") Flow chosunArticleCollectFlow,
      @Qualifier("hankyungArticleCollectFlow") Flow hankyungArticleCollectFlow) {
    return new FlowBuilder<Flow>("collectArticlesSplitFlow")
        .start(naverArticleCollectFlow)
        .split(taskExecutor()).add(chosunArticleCollectFlow, hankyungArticleCollectFlow)
        .build();
  }

  @Bean(name = "interestsAndUrlsFetchStep")
  public Step interestsAndUrlsFetchStep(InterestRepository interestRepository,
      InterestContainer interestContainer, ArticleRepository articleRepository) {

    return new StepBuilder("interestsFetchStep", jobRepository)
        .tasklet((contribution, chunkContext) -> {

          // 시간날 때 dto로 한방 쿼리
          List<Interest> interestList = interestRepository.findAll();
          List<String> sourceUrls = articleRepository.findAllSourceUrl();
          interestContainer.register(interestList, sourceUrls);

          return RepeatStatus.FINISHED;
        }, transactionManager)
        .build();
  }


  /**
   * Article API Collect Flows
   */
  @Bean
  public Flow naverArticleCollectFlow(
      @Qualifier("naverApiCallTasklet") Tasklet naverApiCallTasklet,
      @Qualifier("naverPromotionListener") ExecutionContextPromotionListener promotionListener) {

    // 호출
    TaskletStep naverArticleCollectStep = new StepBuilder("naverArticleCollectStep", jobRepository)
        .tasklet(naverApiCallTasklet, transactionManager)
        .listener(promotionListener)
        .build();

    return new FlowBuilder<Flow>("naverCollectFlow")
        .start(naverArticleCollectStep)
        .build();
  }

  @Bean
  public Flow chosunArticleCollectFlow(
      @Qualifier("chosunApiCallTasklet") Tasklet chosunApiCallTasklet,
      @Qualifier("chosunPromotionListener") ExecutionContextPromotionListener promotionListener) {

    // 호출
    TaskletStep naverArticleCollectStep = new StepBuilder("chosunArticleCollectStep", jobRepository)
        .tasklet(chosunApiCallTasklet, transactionManager)
        .listener(promotionListener)
        .build();

    return new FlowBuilder<Flow>("chosunCollectFlow")
        .start(naverArticleCollectStep)
        .build();
  }


  @Bean
  public Flow hankyungArticleCollectFlow(
      @Qualifier("hankyungApiCallTasklet") Tasklet hankyungApiCallTasklet,
      @Qualifier("hankyungPromotionListener") ExecutionContextPromotionListener promotionListener) {

    // 호출
    TaskletStep naverArticleCollectStep = new StepBuilder("hankyungArticleCollectStep",
        jobRepository)
        .tasklet(hankyungApiCallTasklet, transactionManager)
        .listener(promotionListener)
        .build();

    return new FlowBuilder<Flow>("hankyungCollectFlow")
        .start(naverArticleCollectStep)
        .build();
  }


  /**
   * Article 모두 처리
   */
  @Bean
  @JobScope
  public Step articleHandlerStep(
      @Qualifier("articleCollectionsReader") ItemReader<ArticleApiDto> articleApiDtoItemReader,
      @Qualifier("collectArticleProcessor") ItemProcessor<ArticleApiDto, ArticleWithInterestList> naverArticleCollectProcessor,
      @Qualifier("articleWithInterestsJdbcItemWriter") ItemWriter<ArticleWithInterestList> articleJdbcItemWriter,
      @Qualifier("naverContextCleanupListener") StepExecutionListener naverContextCleanupListener) {

    return new StepBuilder("articleHandlerStep", jobRepository)
        .<ArticleApiDto, ArticleWithInterestList>chunk(200, transactionManager)
        .reader(articleApiDtoItemReader)
        .processor(naverArticleCollectProcessor)
        .writer(articleJdbcItemWriter)
        .faultTolerant()
        .retryLimit(3)
        .retry(Exception.class)
        .listener(naverContextCleanupListener)
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
