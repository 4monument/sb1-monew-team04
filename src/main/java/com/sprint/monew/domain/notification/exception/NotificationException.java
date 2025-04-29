package com.sprint.monew.domain.notification.exception;

import com.sprint.monew.global.error.ErrorCode;
import com.sprint.monew.global.error.MonewException;

public class NotificationException extends MonewException {

  public NotificationException(ErrorCode errorCode) {
    super(errorCode);
  }

  public NotificationException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
