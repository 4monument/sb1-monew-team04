package com.sprint.monew.common.batch.articlecollect;

import static com.sprint.monew.common.batch.util.CustomExecutionContextKeys.*;

import com.sprint.monew.common.batch.util.ExecutionContextFinder;
import com.sprint.monew.common.batch.util.Keywords;
import com.sprint.monew.domain.interest.InterestRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ArticleCollectBatch {

  private final EntityManagerFactory emf;
  private final PlatformTransactionManager transactionManager;
  private final JobRepository jobRepository;

  @Bean
  public Job articleCollectJob(
      @Qualifier("fetchKeywordsStep") Step articleCollectStep,
      @Qualifier("naverArticleCollectFlow") Flow naverArticleCollectFlow) {

    return new JobBuilder("articleCollectJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .start(articleCollectStep)
        .on(null) // 나중
        .to(naverArticleCollectFlow)
        //.split( ) 나중에 multi flow 구현
        .end()
        .build();
  }

  // keyword들을 전부 가져와서 스텝끼리 공유 가능한 저장소에 저장
  @Bean(name = "fetchKeywordsStep")
  @JobScope
  public Step fetchKeywordsStep(InterestRepository interestRepository) {
    return new StepBuilder("KeyWordCollectStep", jobRepository)
        .tasklet((contribution, chunkContext) -> {
          Keywords allKeyword = interestRepository.findAllKeyword();
          ExecutionContext jobExecutionContext = ExecutionContextFinder.findJobExecutionContext(
              contribution);
          jobExecutionContext.put(KEYWORDS.getKey(), allKeyword);
          return RepeatStatus.FINISHED;
        }, transactionManager)
        .build();
  }
}
