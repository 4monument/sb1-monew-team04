//package com.sprint.monew.common.batch.temp;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.Resource;
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobParameters;
//import org.springframework.batch.core.JobParametersBuilder;
//import org.springframework.batch.core.JobParametersInvalidException;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
//import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
//import org.springframework.batch.core.repository.JobRestartException;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class tempArticleCollect {
//
//  private final JobLauncher jobLauncher;
//
//  @Resource(name = "articleCollectJob")
//  private final Job job;
//
//  @PostConstruct
//  public void collectArticles()
//      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
//    JobParameters jobParameters = new JobParametersBuilder()
//        .addLong("time", System.currentTimeMillis())
//        .toJobParameters();
//
//    jobLauncher.run(job, jobParameters);
//  }
//}
