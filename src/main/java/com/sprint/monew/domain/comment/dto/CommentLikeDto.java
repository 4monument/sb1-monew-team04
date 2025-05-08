package com.sprint.monew.domain.comment.dto;

import com.sprint.monew.domain.comment.Comment;
import com.sprint.monew.domain.comment.like.Like;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

public record CommentLikeDto(
    @Schema(description = "좋아요 ID")
    UUID id,
    @Schema(description = "좋아요한 유저 ID")
    UUID likedBy,
    @Schema(description = "좋아요한 날짜")
    Instant createdAt,
    @Schema(description = "댓글 ID")
    UUID commentId,
    @Schema(description = "기사 ID")
    UUID articleId,
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

  public static CommentLikeDto from(Like like, Long commentLikeCount) {
    Comment comment = like.getComment();
    return new CommentLikeDto(
        like.getId(),
        like.getUser().getId(),
        like.getCreatedAt(),
        comment.getId(),
        comment.getArticle().getId(),
        comment.getUser().getId(),
        comment.getUser().getNickname(),
        comment.getContent(),
        commentLikeCount,
        comment.getCreatedAt()
    );
  }
}
