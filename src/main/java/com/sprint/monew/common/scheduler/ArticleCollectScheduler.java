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

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleCollectScheduler {

  //private final ArticleService articleService;

  @Resource(name = "articleCollectJob")
  private Job articleCoolectJob;
  private final JobLauncher jobLauncher;

  @Scheduled(cron = "0 0 0/1 * * ?", zone = "Asia/Seoul")
  public void collectArticles()
      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("time", System.currentTimeMillis())
        .toJobParameters();

    log.info("Article collection start");
    jobLauncher.run(articleCoolectJob, jobParameters);

    JobParameters notificationJobParameters = new JobParametersBuilder()
        .addLong("time", System.currentTimeMillis())
        .toJobParameters();

    //jobLauncher.run()
  }
}
