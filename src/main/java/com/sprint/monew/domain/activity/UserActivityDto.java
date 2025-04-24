package com.sprint.monew.domain.activity;

import com.sprint.monew.domain.article.dto.ArticleViewDto;
import com.sprint.monew.domain.comment.dto.CommentDto;

import java.util.List;
import java.util.UUID;

public record UserActivityDto(
        UUID userId,
        String username,
        List<String> subscribedInterests,
        List<CommentDto> recentComments,
        List<CommentDto> likedComments,
        List<ArticleViewDto> recentViewedNews
) {}
