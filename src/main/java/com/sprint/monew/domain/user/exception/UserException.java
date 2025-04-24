package com.sprint.monew.domain.user.exception;

import com.sprint.monew.global.error.ErrorCode;
import com.sprint.monew.global.error.MonewException;

public class UserException extends MonewException {
  public UserException(ErrorCode errorCode) {
    super(errorCode);
  }

  public UserException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}