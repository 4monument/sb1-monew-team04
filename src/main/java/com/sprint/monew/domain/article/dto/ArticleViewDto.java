package com.sprint.monew.domain.article.dto;

import com.sprint.monew.domain.article.Article;
import java.time.Instant;
import java.util.UUID;

public record ArticleViewDto(
    UUID id,
    UUID viewedBy,
    Instant createdAt,
    UUID articleId,
    String source,
    String sourceUrl,
    String articleTitle,
    Instant articlePublishDate,
    String articleSummary,
    Long articleCommentCount,
    Long articleViewCount
) {

}
