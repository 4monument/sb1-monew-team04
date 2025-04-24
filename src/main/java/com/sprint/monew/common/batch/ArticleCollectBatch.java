package com.sprint.monew.common.batch;

import static com.sprint.monew.common.batch.util.CustomExecutionContextKeys.*;

import com.sprint.monew.common.batch.util.ExecutionContextFinder;
import com.sprint.monew.common.batch.util.Keywords;
import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.interest.InterestRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
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
      @Qualifier("keywordCollectStep") Step articleCollectStep,
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
  @Bean(name = "keywordCollectStep")
  @JobScope
  public Step keywordCollectStep(InterestRepository interestRepository) {
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

  // Naver Flow -> API를 호출 + 처리
  @Bean(name = "naverArticleCollectFlow")
  @StepScope
  public Flow naverArticleCollectFlow(NaverApiCall naverApiCall) {
    // 호출
    TaskletStep naverArticleCollectStep = new StepBuilder("naverArticleCollectStep", jobRepository)
        .tasklet(naverApiCall, transactionManager)
        .build();

    return new FlowBuilder<Flow>("naverCollectFlow")
        .start(naverArticleCollectStep)
        .next(articleHandlerStep()) // 처리  스텝
        .build();
  }

  @Bean
  @JobScope
  public Step articleHandlerStep() {
    // Article을 처리하는 Step
    return new StepBuilder("articleHandlerStep", jobRepository)
        .tasklet((contribution, chunkContext) -> {
          // 나중에 구현
          return RepeatStatus.FINISHED;
        }, transactionManager)
        .build();
  }

  // ItemReader

  // ItemProcessor

  // ItemWriter


  // 성능 안좋은 Writer : JpaItemWriter
  @Bean
  public ItemWriter<Article> articleJpaItemWriter() {
     return new JpaItemWriterBuilder<Article>()
         .usePersist(true)
         .entityManagerFactory(emf)
         .build();
  }

  // 안 쓸 step : QueryDsl로 커스텀한 Step과 비교용으로 만든 간단한 step
  @Bean
  @StepScope
  public Step articleStepByJpaItemWriter() {
    return new StepBuilder("articleStepByJpaItemWriter", jobRepository)
        .<Article, Article>chunk(10, transactionManager)
        .reader(null) // 나중에 구현
        .writer(articleJpaItemWriter()) //
        .build();
  }

}
