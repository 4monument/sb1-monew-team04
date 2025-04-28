package com.sprint.monew.common.batch.util;

import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.Article.Source;
import com.sprint.monew.domain.article.articleinterest.ArticleInterest;
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

  public Article toArticle(){
    return Article.create(
        source,
        sourceUrl,
        title,
        publishDate,
        summary
    );
  }

  public List<ArticleInterest> toArticleInterests() {
    Article article = toArticle();
    return interestList.stream()
        .map(interest -> ArticleInterest.create(article, interest))
        .toList();
  }
}
