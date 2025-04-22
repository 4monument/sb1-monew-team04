package com.sprint.monew.domain.notification;

import java.time.Instant;
import java.util.UUID;

public record NotificationDto(

    UUID id,
    Instant createdAt,
    Instant updatedAt,
    boolean confirmed,
    UUID userId,
    String content,
    String resourceType,
    UUID resourceId
) {

  public static NotificationDto from(Notification notification) {
    return new NotificationDto(
        notification.getId(),
        notification.getCreatedAt(),
        notification.getUpdatedAt(),
        notification.isConfirmed(),
        notification.getUser().getId(),
        notification.getContent(),
        notification.getResourceType().toString(),
        notification.getResourceId()
    );
  }

}
