package com.sprint.monew.domain.user.exception;

import com.sprint.monew.global.error.ErrorCode;
import java.util.UUID;

public class UserNotFoundException extends UserException {

  public UserNotFoundException() {
    super(ErrorCode.USER_NOT_FOUND);
  }

  public static UserNotFoundException withId(UUID userId) {
    UserNotFoundException exception = new UserNotFoundException();
    exception.addDetail("userId", userId);
    return exception;
  }

  public static UserNotFoundException withEmail(String email) {
    UserNotFoundException exception = new UserNotFoundException();
    exception.addDetail("email", email);
    return exception;
  }
}