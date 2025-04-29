package com.sprint.monew.common.batch;


import com.sprint.monew.common.batch.support.NotificationJdbc;
import com.sprint.monew.domain.interest.subscription.SubscriptionRepository;
import com.sprint.monew.domain.notification.NotificationService;
import com.sprint.monew.domain.notification.dto.UnreadInterestArticleCount;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class NotificationCreateBatch {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final SubscriptionRepository subscriptionRepository;
  private final NotificationService notificationService;
  private final DataSource dataSource;

  @Bean
  public Job notificationCreateJob(@Qualifier("notificationCreateStep") Step notificationCreateStep) {
    return new JobBuilder("notificationCreateJob", jobRepository)
        .start(notificationCreateStep)
        .build();
  }

  @Bean
  @JobScope
  public Step notificationCreateStep(
      @Qualifier("notificationCreateReader") ItemReader<UnreadInterestArticleCount> reader,
      @Qualifier("notificationCreateProcessor") ItemProcessor<UnreadInterestArticleCount, NotificationJdbc> processor,
      @Qualifier("notificationCreateWriter") ItemWriter<NotificationJdbc> writer) {
    return new StepBuilder("notificationCreateStep", jobRepository)
        .<UnreadInterestArticleCount, NotificationJdbc>chunk(500, transactionManager)
        .reader(notificationCreateReader())
        .processor(processor)
        .writer(writer)
        .build();
  }

  @Bean
  @StepScope
  public ItemReader<UnreadInterestArticleCount> notificationCreateReader() {
    Instant afterAt = Instant.now().minus(Duration.ofMinutes(30));
    List<UnreadInterestArticleCount> newArticleCountWithUserInterest =
        subscriptionRepository.findNewArticleCountWithUserInterest(afterAt);
    return new ListItemReader<>(newArticleCountWithUserInterest);
  }

  @Bean
  @StepScope
  public ItemProcessor<UnreadInterestArticleCount, NotificationJdbc> notificationCreateProcessor() {
    return notificationService::createArticleInterestNotifications;
  }

  @Bean
  @StepScope
  public ItemWriter<NotificationJdbc> notificationCreateWriter() {
    String notificationInsertSql =
        "INSERT INTO notifications (id, user_id, resource_id, resource_type, content, created_at, updated_at, confirmed)"
            +
            " VALUES (:id, userId, resourceId, resourceType, content, createdAt, updatedAt, confirmed)";

    return new JdbcBatchItemWriterBuilder<NotificationJdbc>()
        .dataSource(dataSource)
        .assertUpdates(false)
        .sql(notificationInsertSql)
        .columnMapped()
        .build();
  }
}
