package com.sprint.monew.common.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleCollectScheduler {

  //private final ArticleService articleService;
  private final JobLauncher jobLauncher;

  @Scheduled(cron = "0 0 0/1 * * ?", zone = "Asia/Seoul")
  public void collectArticles() {
    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("time", System.currentTimeMillis())
        .toJobParameters();

    log.info("Article collection start");
    //jobLauncher.run(newJob(), jobParameters);
    //나중에 Job정의 후
  }
}
