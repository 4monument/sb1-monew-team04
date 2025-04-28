package com.sprint.monew.domain.comment.dto;

import com.sprint.monew.domain.comment.Comment;
import com.sprint.monew.domain.comment.like.Like;
import java.time.Instant;
import java.util.UUID;

public record CommentLikeDto(
    UUID id,
    UUID likedBy,
    Instant createdAt,
    UUID commentId,
    UUID articleId,
    UUID commentUserId,
    String commentUserNickname,
    String commentContent,
    long commentLikeCount,
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
