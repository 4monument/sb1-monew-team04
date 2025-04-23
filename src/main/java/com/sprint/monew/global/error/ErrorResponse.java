package com.sprint.monew.global.error;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public record ErrorResponse (
  Instant timestamp,
  String code,
  String message,
  Map<String, Object> details,
  int status
) {
  public static ErrorResponse fromMonewEx(MonewException exception) {
    return new ErrorResponse(
        Instant.now(),
        exception.getErrorCode().name(),
        exception.getMessage(),
        exception.getDetails(),
        exception.getErrorCode().getStatus());
  }

  public static ErrorResponse fromEx(Exception exception, int status) {
    return new ErrorResponse(
        Instant.now(),
        exception.getClass().getSimpleName(),
        exception.getMessage(),
        new HashMap<>(),
        status);
  }

  public static ErrorResponse of(ErrorCode errorCode, Map<String, Object> details) {
    return new ErrorResponse(
        Instant.now(),
        errorCode.name(),
        errorCode.getMessage(),
        details,
        errorCode.getStatus()
    );
  }
}
