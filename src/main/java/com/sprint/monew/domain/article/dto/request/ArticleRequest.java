package com.sprint.monew.domain.article.dto.request;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ArticleRequest(
    String keyword,
    UUID interestId,
    List<String> sourceIn,
    Instant publishDateFrom,
    Instant publishDateTo,
    String cursor,
    String after
) {

}
