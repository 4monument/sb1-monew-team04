package com.sprint.monew.common.batch.temp;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TestS3BackUpBatch {

  @Resource(name = "s3BackupJob")
  private Job s3BackupJob;
  private final JobLauncher jobLauncher;

  // 삭제할 파일 : 로컬 테스트 용
  @PostConstruct
  public void testInit() throws Exception {
    JobParameters jobParameters = new JobParametersBuilder()
        //.addString("runDate", Instant.now().toString())
        .addLocalDateTime("dateTime", LocalDateTime.now())
        .toJobParameters();
    jobLauncher.run(s3BackupJob, jobParameters);
  }
}
