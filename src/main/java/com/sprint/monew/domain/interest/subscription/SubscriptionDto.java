package com.sprint.monew.domain.interest.subscription;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SubscriptionDto(
    UUID id,
    UUID interestId,
    String interestName,
    List<String> interestKeywords,
    long interestSubscriberCount,
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
