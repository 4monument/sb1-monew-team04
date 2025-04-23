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
) {}
