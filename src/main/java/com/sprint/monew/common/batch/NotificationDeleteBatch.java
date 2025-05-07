package com.sprint.monew.common.batch;

import com.sprint.monew.domain.notification.NotificationRepository;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
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

  @Bean(name = "notificationDeleteJob")
  public Job notificationDeleteJob(
      @Qualifier("notificationChunkDeleteStep") Step notificationDeleteStep) {
    return new JobBuilder("deleteConfirmedNotificationJob", jobRepository)
        .start(notificationDeleteStep)
        .build();
  }

  @Bean(name = "notificationChunkDeleteStep")
  @JobScope
  public Step notificationChunkDeleteStep(
      @Qualifier("notificationDeleteReader") ListItemReader<Instant> reader) {
    return new StepBuilder("notificationChunkDeleteStep", jobRepository)
        .<Instant, Instant>chunk(1, transactionManager)
        .reader(reader)
        .writer((instants) -> {
          Instant sevenDaysAgo = instants.getItems().get(0);
          doDelete(sevenDaysAgo);
        })
        .faultTolerant()
        .retryLimit(3)
        .retry(Exception.class)
        .build();
  }

  @Bean
  @StepScope
  public ListItemReader<Instant> notificationDeleteReader() {
    Instant threshold = Instant.now().minus(Duration.ofDays(7));
    return new ListItemReader<>(List.of(threshold));
  }

  private void doDelete(Instant updatedAt) {
    notificationRepository.deleteConfirmedNotificationsOlderThan(updatedAt);
  }
}
