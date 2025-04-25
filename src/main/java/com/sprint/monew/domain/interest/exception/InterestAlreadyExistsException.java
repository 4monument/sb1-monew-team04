package com.sprint.monew.domain.interest.exception;

import com.sprint.monew.global.error.ErrorCode;

public class InterestAlreadyExistsException extends InterestException {

  public InterestAlreadyExistsException() {
    super(ErrorCode.INTEREST_ALREADY_EXISTS);
  }

  public static InterestAlreadyExistsException alreadyExistsException() {
    return new InterestAlreadyExistsException();
  }
}
