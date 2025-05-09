package com.sprint.monew.domain.comment.dto;

import com.sprint.monew.domain.comment.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

public record CommentActivityDto (
  @Schema(description = "댓글 ID")
  UUID id,
  @Schema(description = "기사 ID")
  UUID articleId,
  @Schema(description = "기사 제목")
  String articleTitle,
  @Schema(description = "작성자 ID")
  UUID userId,
  @Schema(description = "작성자 닉네임")
  String userNickname,
  @Schema(description = "내용")
  String content,
  @Schema(description = "좋아요 수")
  int likeCount,
  @Schema(description = "댓글 작성 날짜")
  Instant createdAt
) {
  public static CommentActivityDto from(Comment comment) {
    return new CommentActivityDto(
        comment.getId(),
        comment.getArticle().getId(),
        comment.getArticle().getTitle(),
        comment.getUser().getId(),
        comment.getUser().getNickname(),
        comment.getContent(),
        comment.getLikes().size(),
        comment.getCreatedAt()
    );
  }
}