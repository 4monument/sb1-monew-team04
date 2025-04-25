package com.sprint.monew.domain.interest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InterestWithSubscriberWithCount {

  private UUID id;
  private String name;
  private List<String> keywords;
  private Instant createdAt;
  private long subscriberCount;

}
