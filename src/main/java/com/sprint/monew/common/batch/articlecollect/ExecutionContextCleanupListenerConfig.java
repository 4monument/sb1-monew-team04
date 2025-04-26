package com.sprint.monew.common.batch.articlecollect;

import com.sprint.monew.common.batch.util.CustomExecutionContextKeys;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutionContextCleanupListenerConfig {

  private static final String NAVER_ARTICLE_DTOS_KEY = CustomExecutionContextKeys.NAVER_ARTICLE_DTOS.getKey();
  private static final String CHOSUN_ARTICLE_DTOS_KEY = CustomExecutionContextKeys.CHOSUN_ARTICLE_DTOS.getKey();
  private static final String HANKYUNG_ARTICLE_DTOS_KEY = CustomExecutionContextKeys.HANKYUNG_ARTICLE_DTOS.getKey();

  @Bean(name = "naverExecutionContextCleanupListener")
  @StepScope
  public StepExecutionListener naverExecutionContextCleanupListener() {
    return new ExecutionContextCleanupListener(NAVER_ARTICLE_DTOS_KEY);
  }

  @Bean(name = "chosunExecutionContextCleanupListener")
  @StepScope
  public StepExecutionListener chosunExecutionContextCleanupListener() {
    return new ExecutionContextCleanupListener(CHOSUN_ARTICLE_DTOS_KEY);
  }

  @Bean(name = "hankyungExecutionContextCleanupListener")
  @StepScope
  public StepExecutionListener hankyungExecutionContextCleanupListener() {
    return new ExecutionContextCleanupListener(HANKYUNG_ARTICLE_DTOS_KEY);
  }

  public static class ExecutionContextCleanupListener implements StepExecutionListener {

    private final String key;

    public ExecutionContextCleanupListener(String key) {
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
}
