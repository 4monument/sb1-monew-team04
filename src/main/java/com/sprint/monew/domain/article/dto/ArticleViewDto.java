package com.sprint.monew.domain.article.dto;

import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.articleview.ArticleView;
import com.sprint.monew.domain.user.User;
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

  public static ArticleViewDto from(ArticleView articleView, Long commentCount, Long viewCount) {
    Article article = articleView.getArticle();
    User user = articleView.getUser();
    return new ArticleViewDto(
        articleView.getId(),
        user.getId(),
        articleView.getCreatedAt(),
        article.getId(),
        article.getSource().name(),
        article.getSourceUrl(),
        article.getTitle(),
        article.getPublishDate(),
        article.getSummary(),
        commentCount,
        viewCount
    );
  }
}
