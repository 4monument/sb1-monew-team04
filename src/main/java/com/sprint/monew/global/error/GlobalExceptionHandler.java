package com.sprint.monew.global.error;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleMonewException(MonewException exception) {
    ErrorResponse response = ErrorResponse.fromMonewEx(exception);
    return ResponseEntity
        .status(response.status())
        .body(response);
  }

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleException(Exception exception) {
    ErrorResponse errorResponse = ErrorResponse.fromEx(exception, HttpStatus.INTERNAL_SERVER_ERROR.value());
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(errorResponse);
  }

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException exception) {
    Map<String, Object> details = new HashMap<>();
    exception.getBindingResult().getFieldErrors().forEach(fieldError -> {
      details.put(fieldError.getField(), fieldError.getDefaultMessage());
    });

    ErrorResponse response = ErrorResponse.of(ErrorCode.VALIDATION_ERROR, details);
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(response);
  }
}
