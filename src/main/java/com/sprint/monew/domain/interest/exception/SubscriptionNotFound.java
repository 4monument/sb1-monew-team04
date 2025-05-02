package com.sprint.monew.domain.interest.exception;

import com.sprint.monew.global.error.ErrorCode;

public class SubscriptionNotFound extends InterestException {

  public SubscriptionNotFound() {
    super(ErrorCode.SUBSCRIPTION_NOT_FOUND);
  }

  public static SubscriptionNotFound notFound() {
    return new SubscriptionNotFound();
  }
}
