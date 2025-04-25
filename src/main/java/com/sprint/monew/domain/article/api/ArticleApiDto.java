package com.sprint.monew.domain.article.api;

import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.Article.Source;
import java.time.Instant;
import lombok.Builder;

@Builder
public record ArticleApiDto(
    Source source,
    String sourceUrl,
    String title,
    String summary,
    Instant publishDate
) {

  public Article toEntity() {
    return Article.create(
        source,
        sourceUrl,
        title,
        publishDate,
        summary
    );
  }
}
