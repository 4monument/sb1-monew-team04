package com.sprint.monew.common.batch;

import com.sprint.monew.domain.notification.NotificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Instant;

@Configuration
@RequiredArgsConstructor
public class NotificationDeleteBatch {

  private final NotificationRepository notificationRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobRepository jobRepository;

  @Primary
  @Bean(name = "notificationDeleteJob")
  public Job notificationDeleteJob() {
    return new JobBuilder("deleteConfirmedNotificationJob", jobRepository)
        .start(notificationDeleteStep())
        .build();
  }

  @Bean
  public Step notificationDeleteStep() {
    return new StepBuilder("notificationDeleteStep", jobRepository)
        .tasklet(notificationDeleteTasklet(), transactionManager)
        .build();
  }

  // 옵션1. 간단한 테스크를 사용할 경우에 쓸 메서드
  @Bean
  public Tasklet notificationDeleteTasklet() {
    return (contribution, chunkContext) -> {
      Instant sevenDaysAgo = Instant.now().minusSeconds(7 * 24 * 60 * 60); // 1주일 전
      doDelete(sevenDaysAgo);
      return RepeatStatus.FINISHED;
    };
  }

  // 옵션 2. 재시도 옵션 사용 시 Chunk를 사용
  // 어떤 옵션을 쓸지 아직 미정
  @Bean
  public Step notificationChunkDeleteStep() {
     return new StepBuilder("notificationChunkDeleteStep", jobRepository)
         .<Instant, Instant>chunk(1, transactionManager)
         .reader(() -> Instant.now().minusSeconds(7 * 24 * 60 * 60))
         .writer((instants) -> {
           Instant sevenDaysAgo = instants.getItems().get(0);
           doDelete(sevenDaysAgo);
         })
         .faultTolerant()
         .retryLimit(3)
         .retry(Exception.class)
         .build();
  }

  private void doDelete(Instant updatedAt) {
    notificationRepository.deleteConfirmedNotificationsOlderThan(updatedAt);
  }
}
