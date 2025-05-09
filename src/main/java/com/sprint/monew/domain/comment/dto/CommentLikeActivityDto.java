package com.sprint.monew.domain.comment.dto;

import com.sprint.monew.domain.comment.like.Like;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

public record CommentLikeActivityDto (
  @Schema(description = "좋아요 ID")
  UUID id,
  @Schema(description = "좋아요한 날짜")
  Instant createdAt,
  @Schema(description = "댓글 ID")
  UUID commentId,
  @Schema(description = "기사 ID")
  UUID articleId,
  @Schema(description = "기사 제목")
  String articleTitle,
  @Schema(description = "댓글 작성자 ID")
  UUID commentUserId,
  @Schema(description = "댓글 작성자 닉네임")
  String commentUserNickname,
  @Schema(description = "댓글 내용")
  String commentContent,
  @Schema(description = "댓글 좋아요 수")
  long commentLikeCount,
  @Schema(description = "댓글 작성 날짜")
  Instant commentCreatedAt
) {
  public static CommentLikeActivityDto from(Like like, Long commentLikeCount){
    return new CommentLikeActivityDto(
        like.getId(),
        like.getCreatedAt(),
        like.getComment().getId(),
        like.getComment().getArticle().getId(),
        like.getComment().getArticle().getTitle(),
        like.getComment().getUser().getId(),
        like.getComment().getUser().getNickname(),
        like.getComment().getContent(),
        commentLikeCount,
        like.getComment().getCreatedAt()
    );
  }
}
