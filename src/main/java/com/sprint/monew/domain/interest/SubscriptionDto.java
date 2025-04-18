package com.sprint.monew.domain.interest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SubscriptionDto {
  private UUID id; // 항상 null로 반환됨. why?
  private UUID interestId;
  private String interestName;
  private List<String> interestKeywords;
  private int interestSubscriberCount;
  private Instant createdAt;

  public static SubscriptionDto from(Interest interest, int subscriberCount, Instant createdAt ) {
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
