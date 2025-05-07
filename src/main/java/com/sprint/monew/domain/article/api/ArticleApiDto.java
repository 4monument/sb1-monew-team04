package com.sprint.monew.domain.article.api;

import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.Article.Source;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.Instant;
import lombok.Builder;
import lombok.EqualsAndHashCode;

@Builder
public record ArticleApiDto(
    Source source,
    String sourceUrl,
    String title,
    Instant publishDate,
    String summary
) implements Serializable {
  private static final long serialVersionUID = 1L;
}
