package com.sprint.monew.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
  USER_NOT_FOUND(404, "User Not Found");

  private final int status;
  private final String message;
}