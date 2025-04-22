package com.sprint.monew.common.scheduler;


import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class S3BackupDailyScheduler {

  @Resource(name = "s3BackupJob")
  private Job s3BackupJob;

  @Autowired
  private JobLauncher jobLauncher;

  @Scheduled(cron = "0 0 3 * * ?", zone = "Asia/Seoul")
  public void dailyS3Backup()
      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

    LocalDateTime startOfYesterday = LocalDateTime.now().minusDays(1);
    JobParameters jobParameters = new JobParametersBuilder()
        .addLocalDateTime("backupDateTime", startOfYesterday)
        .toJobParameters();

    jobLauncher.run(s3BackupJob, jobParameters);
  }
}
