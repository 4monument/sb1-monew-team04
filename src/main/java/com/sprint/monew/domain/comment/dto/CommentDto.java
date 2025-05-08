package com.sprint.monew.domain.comment.dto;

import com.sprint.monew.domain.comment.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

public record CommentDto(
    @Schema(description = "댓글 ID")
    UUID id,
    @Schema(description = "기사 ID")
    UUID articleId,
    @Schema(description = "작성자 ID")
    UUID userId,
    @Schema(description = "작성자 닉네임")
    String userNickname,
    @Schema(description = "내용")
    String content,
    @Schema(description = "좋아요 수")
    int likeCount,
    @Schema(description = "사용자가 좋아요를 눌렀는지 여부")
    boolean likedByMe,
    @Schema(description = "댓글 작성 날짜")
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
