package com.sprint.monew.domain.interest.dto;

import com.sprint.monew.domain.interest.Interest;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SubscriptionDto(
    //UserInterest 의 id가 복합키이기때문에 String 으로 변경
    String id,
    UUID interestId,
    String interestName,
    List<String> interestKeywords,
    int interestSubscriberCount,
    Instant createdAt
) {

  public static SubscriptionDto from(String userInterestId, Interest interest,
      int subscriberCount) {
    return new SubscriptionDto(
        userInterestId,
        interest.getId(),
        interest.getName(),
        interest.getKeywords(),
        subscriberCount,
        interest.getCreatedAt()
    );
  }
}
