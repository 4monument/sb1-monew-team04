package com.sprint.monew.domain.interest.dto;

import com.sprint.monew.domain.interest.Interest;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SubscriptionDto(
    UUID id,
    UUID interestId,
    String interestName,
    List<String> interestKeywords,
    int interestSubscriberCount,
    Instant createdAt
) {

  public static SubscriptionDto from(Interest interest, int subscriberCount) {
    return new SubscriptionDto(
        interest.getId(),
        interest.getId(),
        interest.getName(),
        interest.getKeywords(),
        subscriberCount,
        interest.getCreatedAt()
    );
  }
}
