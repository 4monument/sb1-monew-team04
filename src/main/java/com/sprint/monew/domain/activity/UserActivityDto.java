package com.sprint.monew.domain.activity;

import com.sprint.monew.domain.article.dto.ArticleViewDto;
import com.sprint.monew.domain.comment.dto.CommentActivityDto;
import com.sprint.monew.domain.comment.dto.CommentLikeDto;
import com.sprint.monew.domain.interest.subscription.SubscriptionDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserActivityDto(
    @Schema(description = "사용자 활동 내역 ID")
    UUID id,
    @Schema(description = "사용자 email")
    String email,
    @Schema(description = "사용자 닉네임")
    String nickname,
    @Schema(description = "활동 내역 생성일")
    Instant createdAt,
    @Schema(description = "구독 중인 관심사")
    List<SubscriptionDto> subscriptions,
    @Schema(description = "작성한 댓글 목록")
    List<CommentActivityDto> comments,
    @Schema(description = "좋아요한 댓글 목록")
    List<CommentLikeDto> commentLikes,
    @Schema(description = "조회한 기사 목록")
    List<ArticleViewDto> articleViews
) {

  public static UserActivityDto fromDocument(UserActivityDocument document) {
    return new UserActivityDto(
        document.getId(),
        document.getEmail(),
        document.getNickname(),
        document.getCreatedAt(),
        document.getSubscriptions(),
        document.getComments(),
        document.getCommentLikes(),
        document.getArticleViews());
  }

  public static UserActivityDocument toDocument(UserActivityDto dto) {
    return UserActivityDocument.builder()
        .id(dto.id())
        .email(dto.email())
        .nickname(dto.nickname())
        .createdAt(dto.createdAt())
        .subscriptions(dto.subscriptions())
        .comments(dto.comments())
        .commentLikes(dto.commentLikes())
        .articleViews(dto.articleViews())
        .build();
  }
}
