package com.sprint.monew.global.error;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {
    private final Instant timestamp;
    private final String code;
    private final String message;
    private final Map<String, Object> details;
    private final String exceptionType;
    private final int status;

    public ErrorResponse(MonewException exception) {
        this(Instant.now(), exception.getErrorCode().name(), exception.getMessage(), exception.getDetails(), exception.getClass().getSimpleName(),
            exception.getErrorCode().getStatus().value());
    }

    public ErrorResponse(Exception exception, int status) {
        this(Instant.now(), exception.getClass().getSimpleName(), exception.getMessage(), new HashMap<>(), exception.getClass().getSimpleName(), status);
    }
} 
