package com.sprint.monew.domain.notification.dto;

import com.sprint.monew.domain.notification.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

public record NotificationDto(
    @Schema(description = "알림 ID")
    UUID id,
    @Schema(description = "생성된 날짜 ID")
    Instant createdAt,
    @Schema(description = "확인한 날짜 ID")
    Instant updatedAt,
    @Schema(description = "확인 여부")
    boolean confirmed,
    @Schema(description = "알림 대상 사용자 ID")
    UUID userId,
    @Schema(description = "내용")
    String content,
    @Schema(description = "관련한 리소스 유형", allowableValues = {"interest", "comment"})
    String resourceType,
    @Schema(description = "관련된 리소스 ID")
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
