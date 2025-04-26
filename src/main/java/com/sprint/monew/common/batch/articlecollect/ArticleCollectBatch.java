package com.sprint.monew.common.batch.articlecollect;

import static com.sprint.monew.common.batch.util.CustomExecutionContextKeys.*;
import static org.springframework.batch.core.ExitStatus.*;

import com.sprint.monew.common.batch.util.ExecutionContextFinder;
import com.sprint.monew.common.batch.util.Interests;
import com.sprint.monew.domain.interest.InterestRepository;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.FlowStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ArticleCollectBatch {

  private final PlatformTransactionManager transactionManager;
  private final JobRepository jobRepository;

  @Bean(name = "articleCollectJob")
  public Job articleCollectJob(
      @Qualifier("interestsFetchStep") Step interestsFetchStep,
      @Qualifier("naverArticleCollectFlow") Flow naverArticleCollectFlow) {

    return new JobBuilder("articleCollectJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .start(interestsFetchStep)
        .on(COMPLETED.getExitCode())
        .to(naverArticleCollectFlow)
        //.split(taskExecutor()).add(null) // 나중
        .end()
        .build();
  }

  @Bean
  public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setThreadNamePrefix("article-async-thread-");
    return executor;
  }

  // keyword들을 전부 가져와서 스텝끼리 공유 가능한 저장소에 저장
  @Bean(name = "interestsFetchStep")
  @JobScope
  public Step interestsFetchStep(InterestRepository interestRepository) {
    return new StepBuilder("interestsFetchStep", jobRepository)
        .tasklet((contribution, chunkContext) -> {
          ExecutionContext jobExecutionContext = ExecutionContextFinder.findJobExecutionContext(
              contribution);
          Interests interests = new Interests(interestRepository.findAll());
          jobExecutionContext.put(INTERESTS.getKey(), interests);
          return RepeatStatus.FINISHED;
        }, transactionManager)
        .build();
  }
}
