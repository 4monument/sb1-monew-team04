package com.sprint.monew.domain.comment.dto;

import com.sprint.monew.domain.comment.Comment;

import java.time.Instant;
import java.util.UUID;

public record CommentDto(
    UUID id,
    UUID articleId,
    UUID userId,
    String userNickname,
    String content,
    int likeCount,
    boolean likedByMe,
    Instant createdAt
) {

  public static CommentDto from(Comment comment, boolean likedByMe) {
    return new CommentDto(
        comment.getId(),
        comment.getArticle().getId(),
        comment.getUser().getId(),
        comment.getUser().getNickname(),
        comment.getContent(),
        comment.getLikes().size(),
        likedByMe,
        comment.getCreatedAt()
    );
  }
}
