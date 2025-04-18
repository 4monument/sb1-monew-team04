package com.sprint.monew.domain.interest;

import java.util.List;
import java.util.UUID;

public record InterestDto(
    UUID id,
    String name,
    List<String> keywords,
    int subscriberCount,
    boolean subscribedByMe
) {

  public static InterestDto from(Interest interest, int subscriberCount, boolean subscribedByMe) {
    return new InterestDto(
        interest.getId(),
        interest.getName(),
        interest.getKeywords(),
        subscriberCount,
        subscribedByMe
    );
  }
}
