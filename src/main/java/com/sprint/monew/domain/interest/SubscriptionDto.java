package com.sprint.monew.domain.interest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SubscriptionDto(
    UUID id, // 항상 null로 반환됨. why?
    UUID interestId,
    String interestName,
    List<String> interestKeywords,
    int interestSubscriberCount,
    Instant createdAt
) {

  public static SubscriptionDto from(Interest interest, int subscriberCount, Instant createdAt) {
    return new SubscriptionDto(
        null,
        interest.getId(),
        interest.getName(),
        interest.getKeywords(),
        subscriberCount,
        createdAt
    );
  }
}
