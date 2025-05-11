package com.sprint.monew.domain.article.dto;

import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.articleview.ArticleView;
import com.sprint.monew.domain.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

public record ArticleViewDto(
    @Schema(description = "기사 조회 ID")
    UUID id,
    @Schema(description = "조회한 사용자 ID")
    UUID viewedBy,
    @Schema(description = "조회한 날짜")
    Instant createdAt,
    @Schema(description = "조회한 기사 ID")
    UUID articleId,
    @Schema(description = "기사 출처")
    String source,
    @Schema(description = "기사 출처 URL")
    String sourceUrl,
    @Schema(description = "기사 제목")
    String articleTitle,
    @Schema(description = "기사 발행일")
    Instant articlePublishedDate,
    @Schema(description = "기사 요약")
    String articleSummary,
    @Schema(description = "기사 댓글 수")
    Long articleCommentCount,
    @Schema(description = "기사 조회 수")
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
