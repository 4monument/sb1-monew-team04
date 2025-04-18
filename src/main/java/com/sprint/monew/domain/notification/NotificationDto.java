package com.sprint.monew.domain.notification;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NotificationDto {

  private UUID id;
  private Instant createdAt;
  private Instant updatedAt;
  private boolean confirmed;
  private UUID userId;
  private String content;
  private String resourceType;
  private UUID resourceId;

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
