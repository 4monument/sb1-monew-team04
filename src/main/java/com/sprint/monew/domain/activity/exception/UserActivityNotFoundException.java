package com.sprint.monew.domain.activity.exception;

import com.sprint.monew.global.error.ErrorCode;
import com.sprint.monew.global.error.MonewException;
import java.util.UUID;

public class UserActivityNotFoundException extends MonewException {

  public UserActivityNotFoundException() {
    super(ErrorCode.ACTIVITY_NOT_FOUND);
  }

  public static UserActivityNotFoundException withId(UUID id) {
    UserActivityNotFoundException exception = new UserActivityNotFoundException();
    exception.addDetail("UUID", id);
    return exception;
  }
}