package com.sprint.monew.common.scheduler;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationDeleteScheduler {

  private final JobLauncher jobLauncher;

  @Resource(name = "notificationDeleteJob")
  private Job job;

  // 확인한 알림 중 1주일이 경과된 알림은 자동으로 삭제됩니다.
  @Scheduled(cron = "0 0 4 * * ?", zone = "Asia/Seoul")
  public void deleteConfirmedNotification()
      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("time", System.currentTimeMillis())
        .toJobParameters();

    log.info("Delete ConfirmedNotification");
    jobLauncher.run(job, jobParameters);
  }
}
