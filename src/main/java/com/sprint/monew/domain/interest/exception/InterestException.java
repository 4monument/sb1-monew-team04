package com.sprint.monew.domain.interest.exception;

import com.sprint.monew.global.error.ErrorCode;
import com.sprint.monew.global.error.MonewException;

public class InterestException extends MonewException {

  public InterestException(ErrorCode errorCode) {
    super(errorCode);
  }

  public InterestException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
