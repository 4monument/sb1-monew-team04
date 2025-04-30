package com.sprint.monew.domain.interest.dto;

import com.sprint.monew.domain.interest.Interest;
import java.util.List;
import java.util.UUID;

public record InterestDto(
    UUID id,
    String name,
    List<String> keywords,
    long subscriberCount,
    boolean subscribedByMe
) {

  public static InterestDto from(Interest interest, long subscriberCount, boolean subscribedByMe) {
    return new InterestDto(
        interest.getId(),
        interest.getName(),
        interest.getKeywords(),
        subscriberCount,
        subscribedByMe
    );
  }

  public static InterestDto from(InterestSubscriptionInfoDto interestSubscriptionInfoDto,
      boolean subscribedByMe) {
    return from(interestSubscriptionInfoDto.getInterest(),
        interestSubscriptionInfoDto.getSubscriberCount(),
        subscribedByMe
    );
  }
}
