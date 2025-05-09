package com.sprint.monew.domain.article.dto;

import java.time.Instant;
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

}
