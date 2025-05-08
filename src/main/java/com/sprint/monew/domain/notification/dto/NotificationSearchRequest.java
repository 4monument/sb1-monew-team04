package com.sprint.monew.domain.notification.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.Instant;
import java.util.UUID;

public record NotificationSearchRequest(
    UUID cursor,
    Instant after,

    @Min(value = 1, message = "페이지 크기는 최소 1 이상이어야 합니다")
    @Max(value = 100, message = "페이지 크기는 최대 100까지 가능합니다")
    Integer limit
) {

}
