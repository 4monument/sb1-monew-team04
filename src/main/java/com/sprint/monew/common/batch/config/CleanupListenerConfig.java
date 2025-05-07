package com.sprint.monew.common.batch.config;

import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.*;

import com.sprint.monew.common.batch.support.InterestContainer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CleanupListenerConfig {

  @Bean(name = "naverContextCleanupListener")
  public StepExecutionListener naverContextCleanupListener() {
    return new stepExecutionContextCleanupListener(NAVER_ARTICLE_DTOS.getKey());
  }

  @Bean(name = "chosunContextCleanupListener")
  public StepExecutionListener chosunContextCleanupListener() {
    return new stepExecutionContextCleanupListener(CHOSUN_ARTICLE_DTOS.getKey());
  }

  @Bean(name = "hankyungContextCleanupListener")
  public StepExecutionListener hankyungContextCleanupListener() {
    return new stepExecutionContextCleanupListener(HANKYUNG_ARTICLE_DTOS.getKey());
  }

  @Bean(name = "interestContainerCleanupListener")
  public JobExecutionListener interestContainerCleanupListener(InterestContainer interestContainer) {
    return new interestContainerCleanupListener(interestContainer);
  }

  public static class stepExecutionContextCleanupListener implements StepExecutionListener {

    private final String key;

    public stepExecutionContextCleanupListener(String key) {
      this.key = key;
    }

    @AfterStep
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
      ExecutionContext jobExecutionContext = stepExecution.getJobExecution()
          .getExecutionContext();
      jobExecutionContext.remove(key);
      return stepExecution.getExitStatus();
    }
  }

  @RequiredArgsConstructor
  public static class interestContainerCleanupListener implements JobExecutionListener {

    private final InterestContainer interestContainer;

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
      interestContainer.clearBean();
    }
  }
}
