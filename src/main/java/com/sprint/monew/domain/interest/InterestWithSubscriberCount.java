package com.sprint.monew.domain.interest;

import java.time.Instant;
import java.util.UUID;

//QueryDsl 적용 시 class 로 바꿔야 하는지
public interface InterestWithSubscriberCount {

  UUID getId();

  String getName();

  String getKeywords();

  Instant getCreatedAt();

  long getSubscriberCount();
}
