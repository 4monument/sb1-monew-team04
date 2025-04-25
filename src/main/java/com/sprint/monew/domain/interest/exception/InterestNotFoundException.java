package com.sprint.monew.domain.interest.exception;

import com.sprint.monew.global.error.ErrorCode;
import java.util.UUID;

public class InterestNotFoundException extends InterestException {

  public InterestNotFoundException() {
    super(ErrorCode.INTEREST_NOT_FOUND);
  }

  public static InterestNotFoundException withId(UUID interestId) {
    InterestNotFoundException exception = new InterestNotFoundException();
    exception.addDetail("interestId", interestId);
    return exception;
  }
}
