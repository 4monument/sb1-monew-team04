package com.sprint.monew.common.batch.support;

import com.sprint.monew.domain.interest.Interest;
import com.sprint.monew.domain.notification.ResourceType;
import com.sprint.monew.domain.notification.dto.UnreadInterestArticleCount;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record NotificationJdbc(
    UUID id,
    UUID userId,
    UUID resourceId,
    ResourceType resourceType,
    String content,
    Instant createdAt,
    Instant updatedAt,
    boolean confirmed) {

  public static NotificationJdbc create(UnreadInterestArticleCount unreadInterestArticleCount) {
    Instant createdAt = Instant.now();
    return NotificationJdbc.builder()
        .id(UUID.randomUUID())
        .userId(unreadInterestArticleCount.getUser().getId())
        .resourceId(unreadInterestArticleCount.getInterest().getId())
        .resourceType(ResourceType.INTEREST)
        .content(
            unreadInterestArticleCount.getInterest().getName() + "와/과 관련된 기사가 "
                + unreadInterestArticleCount.getArticleCount()
                + "건 등록되었습니다.")
        .createdAt(createdAt)
        .updatedAt(createdAt)
        .confirmed(false)
        .build();
  }
}
