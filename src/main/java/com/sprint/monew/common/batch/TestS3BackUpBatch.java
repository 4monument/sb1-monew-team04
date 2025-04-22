package com.sprint.monew.common.batch;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.time.LocalDate;
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

  // 삭제할 것 : 로컬 테스트 용
  @PostConstruct
  public void testInit() throws Exception {
    // S3Resource에 대한 초기화 작업을 수행합니다.
    JobParameters jobParameters = new JobParametersBuilder()
        .addLocalDate("date", LocalDate.now())
        .toJobParameters();
    jobLauncher.run(s3BackupJob, jobParameters);
  }
}
