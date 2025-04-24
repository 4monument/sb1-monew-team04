package com.sprint.monew.domain.user.exception;

import com.sprint.monew.global.error.ErrorCode;

public class UserAlreadyDeletedException extends UserException {

  public UserAlreadyDeletedException() {
    super(ErrorCode.ALREADY_DELETED_USER);
  }

  public static UserAlreadyDeletedException withUserId(Object userId) {
    UserAlreadyDeletedException exception = new UserAlreadyDeletedException();
    exception.addDetail("userId", userId);
    return exception;
  }
}
