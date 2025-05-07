package com.sprint.monew.common.batch.temp;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@RequiredArgsConstructor
public class TestArticleCollect {

  @Resource(name = "articleCollectJob")
  private Job articleCollectJob;
  private final JobLauncher jobLauncher;

  // 삭제할 파일 : 로컬 테스트 용
  //@Scheduled(fixedRate = 100000)
  public void testInit() throws Exception {
    JobParameters jobParameters = new JobParametersBuilder()
        //.addString("runDate", Instant.now().toString())
        .addLong("dateTime", System.currentTimeMillis())
        .toJobParameters();
    jobLauncher.run(articleCollectJob, jobParameters);
  }
}
