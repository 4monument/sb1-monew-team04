package com.sprint.monew.domain.article.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ArticleCondition(
    String keyword,
    UUID interestId,
    List<String> sourceIn,
    Instant publishDateFrom,
    Instant publishDateTo,
    String cursor,
    Instant after
) {

  public static ArticleCondition create(
      String keyword,
      UUID interestId,
      List<String> sourceIn,
      LocalDateTime publishDateFrom,
      LocalDateTime publishDateTo,
      String cursor,
      Instant after) {

    return new ArticleCondition(
        keyword,
        interestId,
        sourceIn,
        publishDateFrom == null ? null : publishDateFrom.toInstant(java.time.ZoneOffset.UTC),
        publishDateTo == null ? null : publishDateTo.toInstant(java.time.ZoneOffset.UTC),
        cursor,
        after);
  }
}
