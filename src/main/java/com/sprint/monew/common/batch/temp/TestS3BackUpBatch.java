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

  // 삭제할 것 : 로컬 테스트 용
  @PostConstruct
  public void testInit() throws Exception {
    // S3Resource에 대한 초기화 작업을 수행합니다.
    //실제 사용할 것은 주석처리 -> 배치 데이터는 초기화 안돼서 당일 실행된건
    JobParameters jobParameters = new JobParametersBuilder()
        //.addString("runDate", Instant.now().toString())
        .addLocalDateTime("runDate", LocalDateTime.now())
        .toJobParameters();
    jobLauncher.run(s3BackupJob, jobParameters);
  }
}
