package com.sprint.monew.domain.article.dto;

import com.sprint.monew.domain.article.Article;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

public record ArticleDto(
    @Schema(description = "기사 ID")
    UUID id,
    @Schema(description = "기사 등록일")
    Instant createdAt,
    @Schema(description = "출처")
    String source,
    @Schema(description = "원본 기사 URL")
    String sourceUrl,
    @Schema(description = "제목")
    String title,
    @Schema(description = "기사 발행일")
    Instant publishDate,
    @Schema(description = "기사 요약")
    String summary,
    @Schema(description = "댓글 수")
    Long commentCount,
    @Schema(description = "조회 수")
    Long viewCount,
    @Schema(description = "요청자 조회 여부")
    boolean viewedByMe
) {

  public static ArticleDto from(Article article, Long commentCount, Long viewCount,
      boolean viewedByMe) {
    return new ArticleDto(
        article.getId(),
        article.getCreatedAt(),
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

