package com.sprint.monew.domain.interest;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InterestDto {

  private UUID id;
  private String name;
  private List<String> keywords;
  private int subscriberCount;
  private boolean subscribedByMe;

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
