package com.sprint.monew.domain.activity;

import com.sprint.monew.domain.article.dto.ArticleViewDto;
import com.sprint.monew.domain.comment.dto.CommentDto;
import com.sprint.monew.domain.interest.dto.SubscriptionDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserActivityDto(
        UUID id,
        String email,
        String nickname,
        Instant createdAt,
        List<SubscriptionDto> subscriptions,
        List<CommentDto> recentComments,
        List<CommentDto> likedComments,
        List<ArticleViewDto> recentViewedNews
) {
    public static UserActivityDto fromDocument(UserActivityDocument document) {
        return new UserActivityDto(
                document.getId(),
                document.getEmail(),
                document.getNickname(),
                document.getCreatedAt(),
                document.getSubscriptions(),
                document.getRecentComments(),
                document.getLikedComments(),
                document.getRecentViewedNews()
        );
    }

    public static UserActivityDocument toDocument(UserActivityDto dto) {
        return UserActivityDocument.builder()
                .id(dto.id())
                .email(dto.email())
                .nickname(dto.nickname())
                .createdAt(dto.createdAt())
                .subscriptions(dto.subscriptions())
                .recentComments(dto.recentComments())
                .likedComments(dto.likedComments())
                .recentViewedNews(dto.recentViewedNews())
                .build();
    }
}
