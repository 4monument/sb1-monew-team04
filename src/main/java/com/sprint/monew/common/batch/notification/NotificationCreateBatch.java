package com.sprint.monew.common.batch.notification;


import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class NotificationCreateBatch {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;

  @Bean
  public Job notificationCreateJob() {
    return null; // Job configuration goes here
  }

  @Bean
  @JobScope
  public Step notificationCreateStep() {
    return new StepBuilder("notificationCreateStep", jobRepository)
        .<Object, Object>chunk(10, transactionManager)
        .reader(notificationCreateReader())
        .processor(notificationCreateProcessor())
        .writer(notificationCreateWriter())
        .build();
  }

  public ItemReader<Object> notificationCreateReader() {
    // 유진님 Notification create관련에 쓰일 재료 가져오는 로직
    List<String> temp = List.of("재료1", "재료2", "재료3");
    ListItemReader<String> tempReader = new ListItemReader<>(temp);
    return null;
  }

  public ItemProcessor<Object, Object> notificationCreateProcessor() {
    // 유진님이 쓰는 로직 사용. I/O 작업은 하지 않도록 설계
    return null;
  }

  public ItemWriter<Object> notificationCreateWriter() {
    // 만든거 저장하는 로직
    return null;
  }
}
