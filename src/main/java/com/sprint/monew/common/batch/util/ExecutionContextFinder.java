package com.sprint.monew.common.batch.util;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.item.ExecutionContext;

public class ExecutionContextFinder {

  public static ExecutionContext findJobExecutionContext(StepContribution contribution) {
    return contribution.getStepExecution()
        .getJobExecution()
        .getExecutionContext();
  }

  public static ExecutionContext findStepExecutionContext(StepContribution contribution) {
    return contribution.getStepExecution()
        .getExecutionContext();
  }
}
