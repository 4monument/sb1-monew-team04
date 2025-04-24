package com.sprint.monew.domain.interest.exception;

import com.sprint.monew.global.error.ErrorCode;
import com.sprint.monew.global.error.MonewException;

public class InterestNotFoundException extends MonewException {

  public InterestNotFoundException() {
    super(ErrorCode.INTEREST_NOT_FOUND);
  }

  public static InterestNotFoundException notFound() {
    return new InterestNotFoundException();
  }
}
