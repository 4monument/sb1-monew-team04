package com.sprint.monew.domain.user.exception;

import com.sprint.monew.global.error.ErrorCode;

public class EmailAlreadyExistsException extends UserException {

  public EmailAlreadyExistsException() {
    super(ErrorCode.DUPLICATE_USER);
  }

  public static EmailAlreadyExistsException withEmail(String email) {
    EmailAlreadyExistsException exception = new EmailAlreadyExistsException();
    exception.addDetail("email", email);
    return exception;
  }
} 