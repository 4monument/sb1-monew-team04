package com.sprint.monew.domain.user.exception;

import com.sprint.monew.global.error.ErrorCode;

public class InvalidCredentialsException extends UserException {

  public InvalidCredentialsException() {
    super(ErrorCode.INVALID_USER_CREDENTIALS);
  }

  public static InvalidCredentialsException wrongPassword() {
    InvalidCredentialsException exception = new InvalidCredentialsException();
    return exception;
  }
} 