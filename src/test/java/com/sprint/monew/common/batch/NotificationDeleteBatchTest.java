package com.sprint.monew.common.batch;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.sprint.monew.common.batch.config.BatchTestConfig;
import com.sprint.monew.domain.notification.NotificationRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;


//@Transactional
@BatchTestConfig
@SpringBootTest(
    classes = {NotificationDeleteBatch.class},
    properties = {
        "spring.main.allow-bean-definition-overriding=true",
    }
)
@ActiveProfiles("test")
@Sql(//,
    scripts = {"classpath:schema-batch-test.sql"},
    executionPhase = ExecutionPhase.BEFORE_TEST_METHOD
)
@Import(NotificationDeleteBatch.class)
@Testcontainers
class NotificationDeleteBatchTest {

  @Container
  static PostgreSQLContainer postgres = new PostgreSQLContainer<>(
      DockerImageName.parse("postgres:16-alpine"))
      .withDatabaseName("test-db")
      .withUsername("test-user")
      .withPassword("test-password");

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry){
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);

  }

  @Autowired
  //@Qualifier("s3BackupJobLauncherTestUtils")
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

    // when
    JobExecution jobExecution = jobLauncherTestUtils.launchJob();
    System.out.println(
        "jobExecution.getJobInstance().getJobName() = " + jobExecution.getJobInstance()
            .getJobName());
    // then
    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
  }

  //step 단위 테스트는 나중에 좀 더 알아보기
  @DisplayName("step 테스트")
  @Test
  void testNotificationDeleteStep() {
    jobLauncherTestUtils.setJob(notificationDeleteJob);
    String stepName = "notificationDeleteStep";

    // when
    JobExecution jobExecution = jobLauncherTestUtils.launchStep(stepName);

    StepExecution stepExecution = ((List<StepExecution>) jobExecution.getStepExecutions()).get(0);

    // then
    verify(notificationRepository, times(1))
        .deleteConfirmedNotificationsOlderThan(any());

    assertThat(stepExecution.getStepName()).isEqualTo(stepName);
    assertThat(stepExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
    assertThat(stepExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
  }

//  @TestConfiguration
//  static class JobLauncherTestUtilsTestConfig {
//
//    @Bean
//    @Primary
//    public JobLauncherTestUtils jobLauncherTestUtils(
//        @Qualifier("notificationDeleteJob") Job notificationDeleteJob
//    ) {
//      JobLauncherTestUtils utils = new JobLauncherTestUtils();
//      utils.setJob(notificationDeleteJob);
//      return utils;
//    }
//  }
}