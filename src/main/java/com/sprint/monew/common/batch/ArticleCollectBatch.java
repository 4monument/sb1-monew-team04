package com.sprint.monew.common.batch;

import static org.springframework.batch.core.ExitStatus.*;

import com.sprint.monew.common.batch.support.ArticleWithInterestList;
import com.sprint.monew.common.batch.support.InterestSingleton;
import com.sprint.monew.domain.article.api.ArticleApiDto;
import com.sprint.monew.domain.article.repository.ArticleRepository;
import com.sprint.monew.domain.interest.Interest;
import com.sprint.monew.domain.interest.InterestRepository;
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
      @Qualifier("interestsAndUrlsFetchStep") Step interestsFetchStep,
      @Qualifier("naverArticleCollectFlow") Flow naverArticleCollectFlow,
      @Qualifier("articleCollectJobContextCleanupListener") JobExecutionListener jobContextCleanupListener,
      @Qualifier("localBackupArticlesStep") Step localBackupStep,
      @Qualifier("uploadS3ArticleDtosStep") Step s3BackupStep,
      @Qualifier("naverArticleHandlerStep") Step naverArticleHandlerStep) {

    return new JobBuilder("articleCollectJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .start(interestsFetchStep)
        .on(COMPLETED.getExitCode())
        .to(naverArticleCollectFlow)// 1. Article 자료 수집
        //.split(taskExecutor()).add(null) // 여기에 추가 API 호출 되는 Flow 복붙하면 완성
        .next(localBackupStep)
        .next(s3BackupStep)
        .next(naverArticleHandlerStep)
        .end()
        .listener(jobContextCleanupListener)
        .build();
  }

  @Bean(name = "interestsAndUrlsFetchStep")
  @JobScope
  public Step interestsAndUrlsFetchStep(InterestRepository interestRepository,
      @Qualifier("interestsFetchPromotionListener") ExecutionContextPromotionListener promotionListener,
      InterestSingleton interestSingleton, ArticleRepository articleRepository) {

    return new StepBuilder("interestsFetchStep", jobRepository)
        .tasklet((contribution, chunkContext) -> {

          // 시간날 때 dto로 한방 쿼리
          List<Interest> interestList = interestRepository.findAll();
          List<String> sourceUrls = articleRepository.findAllSourceUrl();
          interestSingleton.register(interestList, sourceUrls);

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
  @JobScope
  public Step naverArticleHandlerStep(
      @Qualifier("naverArticleCollectReader") ItemReader<ArticleApiDto> naverArticleCollectReader,
      @Qualifier("articleCollectProcessor") ItemProcessor<ArticleApiDto, ArticleWithInterestList> naverArticleCollectProcessor,
      @Qualifier("articleWithInterestsJdbcItemWriter") ItemWriter<ArticleWithInterestList> articleJdbcItemWriter,
      @Qualifier("naverContextCleanupListener") StepExecutionListener naverContextCleanupListener) {

    return new StepBuilder("articleHandlerStep", jobRepository)
        .<ArticleApiDto, ArticleWithInterestList>chunk(200, transactionManager)
        .reader(naverArticleCollectReader)
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
