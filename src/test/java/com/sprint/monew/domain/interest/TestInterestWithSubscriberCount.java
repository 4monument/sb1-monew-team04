package com.sprint.monew.domain.interest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

class TestInterestWithSubscriberCount implements InterestWithSubscriberCount {
  private UUID id;
  private String name;
  private List<String> keywords;
  private Instant createdAt;
  private Long subscriberCount;

  public TestInterestWithSubscriberCount(UUID id, String name, List<String> keywords,
      Instant createdAt, Long subscriberCount) {
    this.id = id;
    this.name = name;
    this.keywords = keywords;
    this.createdAt = createdAt;
    this.subscriberCount = subscriberCount;
  }

  @Override
  public UUID getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<String> getKeywords() {
    return keywords;
  }

  @Override
  public Instant getCreatedAt() {
    return createdAt;
  }

  @Override
  public long getSubscriberCount() {
    return subscriberCount;
  }
}
