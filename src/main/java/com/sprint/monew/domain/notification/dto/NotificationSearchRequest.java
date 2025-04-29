package com.sprint.monew.domain.notification.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationSearchRequest(
    UUID cursor,
    Instant after,
    Integer limit
) {

}
