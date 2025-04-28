package com.sprint.monew.common.batch.articlecollect;

import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.*;
import static org.springframework.batch.core.ExitStatus.*;

import com.sprint.monew.common.batch.support.ArticleWithInterestList;
import com.sprint.monew.common.batch.support.Interests;
import com.sprint.monew.domain.article.api.ArticleApiDto;
import com.sprint.monew.domain.interest.InterestRepository;
import com.sprint.monew.global.config.S3ConfigProperties;
import io.awspring.cloud.s3.S3OutputStreamProvider;
import io.awspring.cloud.s3.S3Resource;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
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
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ArticleCollectBatch {

  private final PlatformTransactionManager transactionManager;
  private final JobRepository jobRepository;
  private final S3ConfigProperties s3Properties;
  private final S3Client s3Client;
  private S3Resource articleS3Resource; // 매일 변하는 변수(백업 하는 날마다 PATH가 달라짐)

  @Bean(name = "articleCollectJob")
  public Job articleCollectJob(
      @Qualifier("interestsFetchStep") Step interestsFetchStep,
      @Qualifier("naverArticleCollectFlow") Flow naverArticleCollectFlow,
      @Qualifier("articleCollectJobContextCleanupListener") JobExecutionListener jobContextCleanupListener) {

    return new JobBuilder("articleCollectJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .start(interestsFetchStep)
        .on(COMPLETED.getExitCode())
        .to(naverArticleCollectFlow)
        //.split(taskExecutor()).add(null) // 나중
        .end()
        .listener(jobContextCleanupListener)
        .build();
  }

  @Bean(name = "interestsFetchStep")
  @JobScope
  public Step interestsFetchStep(
      InterestRepository interestRepository,
      S3OutputStreamProvider s3OutputStreamProvider,
      @Qualifier("interestsFetchPromotionListener") ExecutionContextPromotionListener promotionListener) {

    return new StepBuilder("interestsFetchStep", jobRepository)
        .tasklet((contribution, chunkContext) -> {

          LocalDate backupTargetDate = LocalDate.now();
          articleS3Resource = createS3Resource(backupTargetDate, s3OutputStreamProvider);

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
        .next(backupNaverArticlesToS3Step())
        .next(articleHandlerStep) // 처리  스텝
        .build();
  }

  @Bean
  @StepScope // 기사 수집한 거 백업
  public Step backupNaverArticlesToS3Step() {
    return new StepBuilder("s3BackupStep", jobRepository)
        .tasklet((contribution, chunkContext) -> {

          ExecutionContext jobContext = contribution.getStepExecution().getJobExecution()
              .getExecutionContext();

          List<ArticleApiDto> articleApiDtos;
          try {
            articleApiDtos = (List<ArticleApiDto>) jobContext.get(NAVER_ARTICLE_DTOS.getKey());
          } catch (ClassCastException e) {
            throw new RuntimeException("ExecutionContext로부터 ArticleApiDto를 Casting하는데 실패했습니다.");
          }

          backupArticlesToS3File(articleApiDtos);

          return RepeatStatus.FINISHED;
        }, transactionManager)
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

  /**
   * 편의 메서드
   */
  private S3Resource createS3Resource(LocalDate backupTargetDate,
      S3OutputStreamProvider s3OutputStreamProvider) {
    String fileName = backupTargetDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv";
    String location = "s3://" + s3Properties.bucket() + "/" + fileName;
    return S3Resource.create(location, s3Client, s3OutputStreamProvider);
  }

  private void backupArticlesToS3File(List<ArticleApiDto> articleApiDtos) throws IOException {
    if (articleApiDtos == null || articleApiDtos.isEmpty()) {
      throw new RuntimeException("백업할 기사가 없습니다");
    }

    try (BufferedOutputStream writer =
        new BufferedOutputStream(articleS3Resource.getOutputStream())) {
      log.info("S3 Backup Writer Run");
      for (ArticleApiDto item : articleApiDtos) {
        writer.write(String.format("%s|%s|%s|%s|%s\n",
            item.source(),
            item.sourceUrl(),
            item.title(),
            item.publishDate(),
            item.summary()).getBytes());
      }
    }
  }
}
