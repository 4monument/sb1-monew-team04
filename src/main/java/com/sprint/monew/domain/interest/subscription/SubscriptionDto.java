package com.sprint.monew.domain.interest.subscription;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SubscriptionDto(
    @Schema(description = "구독 정보 ID")
    UUID id,
    @Schema(description = "관심사 ID")
    UUID interestId,
    @Schema(description = "관심사 이름")
    String interestName,
    @Schema(description = "관련 키워드 목록")
    List<String> interestKeywords,
    @Schema(description = "구독자 수")
    long interestSubscriberCount,
    @Schema(description = "구독한 날짜")
    Instant createdAt
) {

  public static SubscriptionDto from(Subscription subscribe,
      int subscriberCount) {
    return new SubscriptionDto(
        subscribe.getId(),
        subscribe.getInterest().getId(),
        subscribe.getInterest().getName(),
        subscribe.getInterest().getKeywords(),
        subscriberCount,
        subscribe.getInterest().getCreatedAt()
    );
  }
}
