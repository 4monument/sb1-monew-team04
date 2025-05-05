package com.sprint.monew.common.batch.support;

import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.interest.Interest;
import java.time.Instant;
import java.util.UUID;

public record ArticleInterestJdbc(
    UUID id,
    UUID articleId,
    UUID interestId,
    Instant createdAt) {

  public static ArticleInterestJdbc create(UUID articleId, UUID interestId) {
    UUID id = UUID.randomUUID();
    return new ArticleInterestJdbc(
        id,
        articleId,
        interestId,
        Instant.now()
    );
  }
}
