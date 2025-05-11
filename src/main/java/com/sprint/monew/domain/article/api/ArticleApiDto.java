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

  public static ArticleApiDto toEscapedArticleApiDto(ArticleApiDto dto) {
    String title = dto.title;
    String summary = dto.summary;
    title = title.replaceAll("\"", "^");
    summary = summary.replaceAll("\"", "^");
    return new ArticleApiDto(
        dto.source,
        dto.sourceUrl,
        title,
        dto.publishDate,
        summary
    );
  }

  public static ArticleApiDto unEscapedArticleApiDto(ArticleApiDto dto) {
    String replacedSummary = dto.summary.replaceAll("\\^", "\"");
    String replacedTitle = dto.title.replaceAll("\\^", "\"");

    return new ArticleApiDto(
        dto.source,
        dto.sourceUrl,
        replacedTitle,
        dto.publishDate,
        replacedSummary
    );
  }
}
