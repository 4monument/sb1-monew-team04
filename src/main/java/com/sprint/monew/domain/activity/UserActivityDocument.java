package com.sprint.monew.domain.activity;

import com.sprint.monew.domain.article.dto.ArticleViewDto;
import com.sprint.monew.domain.comment.dto.CommentDto;
import com.sprint.monew.domain.interest.dto.SubscriptionDto;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "user_activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserActivityDocument {
    @Id
    private UUID id;

    private String email;

    private String nickname;

    private Instant createdAt;

    @Builder.Default
    private List<SubscriptionDto> subscriptions = new ArrayList<>();

    @Builder.Default
    private List<CommentDto> recentComments = new ArrayList<>();

    @Builder.Default
    private List<CommentDto> likedComments = new ArrayList<>();

    @Builder.Default
    private List<ArticleViewDto> recentViewedNews = new ArrayList<>();
}
