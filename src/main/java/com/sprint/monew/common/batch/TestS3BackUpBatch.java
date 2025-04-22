package com.sprint.monew.common.batch;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestS3BackUpBatch {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  @Qualifier("s3BackupJob")
  private Job s3BackupJob;

  // 삭제할 것 : 로컬에서 테스트 편하라고 만든 것
  @PostConstruct
  public void test() throws Exception {
    // S3Resource에 대한 초기화 작업을 수행합니다.
    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("date", System.currentTimeMillis())
        .toJobParameters();

    jobLauncher.run(s3BackupJob, jobParameters);
  }
}
