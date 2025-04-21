package com.sprint.monew.common.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class JobLauncherTestUtilsConfig {

  @Bean(name = "s3BackupJobLauncherTestUtils")  // ← 이름만 변경
  @Primary
  JobLauncherTestUtils jobLauncherTestUtils(
      @Qualifier("s3BackupJob") Job s3BackupJob) {

    JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();
    jobLauncherTestUtils.setJob(s3BackupJob);
    return jobLauncherTestUtils;
  }
}
