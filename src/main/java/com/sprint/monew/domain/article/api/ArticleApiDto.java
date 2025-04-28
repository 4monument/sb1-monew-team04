package com.sprint.monew.domain.article.api;

import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.Article.Source;
import java.io.Serializable;
import java.time.Instant;
import lombok.Builder;
import lombok.EqualsAndHashCode;

@Builder
public record ArticleApiDto(
    Source source,
    String sourceUrl,
    String title,
    String summary,
    Instant publishDate
) implements Serializable {
  private static final long serialVersionUID = 1L;

  public Article toEntity() {
    return Article.create(
        source,
        sourceUrl,
        title,
        publishDate,
        summary
    );
  }
//
//  public ArticleAndInterestsDTO toArticleAndInterestsDTO(List<Interest> interests) {
//    return new ArticleAndInterestsDTO(
//        this.source,
//        this.sourceUrl,
//        this.title,
//        this.publishDate,
//        this.summary,
//        interests
//    );
//  }
}
