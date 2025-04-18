package com.sprint.monew.common.batch;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.sprint.monew.domain.notification.NotificationRepository;
import java.util.Collection;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@SpringBatchTest
class NotificationDeleteBatchTest {

  @Autowired
  JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  @Qualifier("notificationDeleteJob")
  Job notificationDeleteJob;

  @MockitoBean
  NotificationRepository notificationRepository;

  @DisplayName("Job 테스트")
  @Test
  void testNotificationDeleteJob() throws Exception {
    // given
    jobLauncherTestUtils.setJob(notificationDeleteJob);
    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("currentTime", System.currentTimeMillis())
        .toJobParameters();

    // when
    JobExecution jobExecution = jobLauncherTestUtils.launchJob();

    // then
    assertThat(jobExecution.getJobParameters()).isEqualTo(jobParameters);
    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
  }

  //step 단위 테스트는 나중에 좀 더 알아보기
  @DisplayName("step 테스트")
  @Test
  void testNotificationDeleteStep() {
    jobLauncherTestUtils.setJob(notificationDeleteJob);
    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("currentTime", System.currentTimeMillis())
        .toJobParameters();
    String stepName = "notificationDeleteStep";

    // when
    JobExecution jobExecution = jobLauncherTestUtils.launchStep(
        stepName, jobParameters);

    StepExecution stepExecution = ((List<StepExecution>) jobExecution.getStepExecutions()).get(0);

    // then
    verify(notificationRepository, times(1))
        .deleteConfirmedNotificationsOlderThan(any());

    assertThat(stepExecution.getStepName()).isEqualTo(stepName);
    assertThat(stepExecution.getJobParameters()).isEqualTo(jobParameters);
    assertThat(stepExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
    assertThat(stepExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
  }

  @TestConfiguration
  @EnableBatchProcessing
  @Import(NotificationDeleteBatch.class)
  static class BatchTestConfig {
  }
}