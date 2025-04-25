package com.sprint.monew.domain.interest.dto;

import java.time.Instant;
import java.util.UUID;

public record InterestSearchRequest(
    String keyword,
    String orderBy,
    String direction,
    UUID cursor,
    Instant after,
    Integer limit
) {

  public InterestSearchRequest {
    // 기본값 설정
    orderBy = (orderBy == null) ? "createdAt" : orderBy;
    direction = (direction == null) ? "DESC" : direction;
    limit = (limit == null || limit <= 0) ? 50 : limit;
  }

  public static InterestSearchRequest of(String keyword, String orderBy, String direction,
      UUID cursor, Instant after, int limit) {
    return new InterestSearchRequest(keyword, orderBy, direction, cursor, after, limit);
  }
}
