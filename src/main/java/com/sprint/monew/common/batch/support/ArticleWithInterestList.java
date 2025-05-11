package com.sprint.monew.common.batch.support;

import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.Article.Source;
import com.sprint.monew.domain.interest.Interest;
import java.time.Instant;
import java.util.List;

public record ArticleWithInterestList(
    Source source,
    String sourceUrl,
    String title,
    Instant publishDate,
    String summary,
    List<Interest> interestList) {

  public Article toArticleWithId() {
    return Article.createWithId(
        source,
        sourceUrl,
        title,
        publishDate,
        summary
    );
  }
}
