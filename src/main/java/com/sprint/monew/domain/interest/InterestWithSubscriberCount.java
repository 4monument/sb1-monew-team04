package com.sprint.monew.domain.interest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface InterestWithSubscriberCount {
    UUID getId();
    String getName();
    List<String> getKeywords();
    Instant getCreatedAt();
    long getSubscriberCount();
}
