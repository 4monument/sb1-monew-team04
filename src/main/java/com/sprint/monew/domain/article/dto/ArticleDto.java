package com.sprint.monew.domain.article.dto;

import com.sprint.monew.domain.article.Article;
import java.time.Instant;
import java.util.UUID;

public record ArticleDto (
    UUID id,
    String source,
    String sourceUrl,
    String title,
    Instant publishDate,
    String summary,
    Long commentCount,
    Long viewCount,
    boolean viewedByMe
) {
  public static ArticleDto from(Article article, Long commentCount, Long viewCount, boolean viewedByMe) {
    return new ArticleDto(
        article.getId(),
        article.getSource().name(),
        article.getSourceUrl(),
        article.getTitle(),
        article.getPublishDate(),
        article.getSummary(),
        commentCount,
        viewCount,
        viewedByMe
    );
  }
}

