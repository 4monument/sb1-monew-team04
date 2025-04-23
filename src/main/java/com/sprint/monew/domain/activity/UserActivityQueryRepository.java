package com.sprint.monew.domain.activity;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.monew.domain.article.QArticle;
import com.sprint.monew.domain.article.articleview.QArticleView;
import com.sprint.monew.domain.article.dto.ArticleViewDto;
import com.sprint.monew.domain.comment.QComment;
import com.sprint.monew.domain.comment.dto.CommentDto;
import com.sprint.monew.domain.interest.QInterest;
import com.sprint.monew.domain.interest.dto.SubscriptionDto;
import com.sprint.monew.domain.interest.userinterest.QUserInterest;
import com.sprint.monew.domain.like.QLike;
import com.sprint.monew.domain.user.QUser;
import com.sprint.monew.domain.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class UserActivityQueryRepository {

    private final JPAQueryFactory queryFactory;

    public UserActivityDto findUserActivity(UUID userId) {
        QUser user = QUser.user;
        QUserInterest userInterest = QUserInterest.userInterest;
        QInterest interest = QInterest.interest;
        QComment comment = QComment.comment;
        QLike like = QLike.like;
        QArticle article = QArticle.article;
        QArticleView articleView = QArticleView.articleView;

        // 1. 사용자
        User fetchedUser = queryFactory.selectFrom(user)
                .where(user.id.eq(userId))
                .fetchOne();

        if (fetchedUser == null) {
            throw new EntityNotFoundException("사용자 없음");
        }

        // 2. 관심사
        List<SubscriptionDto> subscriptions = queryFactory
                .select(Projections.constructor(SubscriptionDto.class,
                        interest.id,
                        interest.id,
                        interest.name,
                        interest.keywords,
                        interest.id,
                        interest.createdAt))
                .from(userInterest)
                .join(userInterest.interest, interest)
                .where(userInterest.user.id.eq(userId))
                .fetch();

        // 3. 작성한 댓글
        List<CommentDto> comments = queryFactory
                .select(Projections.constructor(CommentDto.class,
                        comment.id,
                        comment.content,
                        comment.createdAt))
                .from(comment)
                .where(comment.user.id.eq(userId))
                .orderBy(comment.createdAt.desc())
                .limit(10)
                .fetch();

        // 4. 좋아요한 댓글
        List<CommentDto> likedComments = queryFactory
                .select(Projections.constructor(CommentDto.class,
                        like.comment.id,
                        like.comment.content,
                        like.comment.createdAt))
                .from(like)
                .where(like.user.id.eq(userId))
                .orderBy(like.user.id.desc())
                .limit(10)
                .fetch();

        // 5. 본 기사 기록
        List<ArticleViewDto> viewedArticles = queryFactory
                .select(Projections.constructor(ArticleViewDto.class,
                        article.id,
                        article.title,
                        articleView.createdAt))
                .from(articleView)
                .join(articleView.article, article)
                .where(articleView.user.id.eq(userId))
                .orderBy(articleView.createdAt.desc())
                .limit(10)
                .fetch();

        return new UserActivityDto(
                userId,
                fetchedUser.getEmail(),
                fetchedUser.getNickname(),
                fetchedUser.getCreatedAt(),
                subscriptions,
                comments,
                likedComments,
                viewedArticles
        );
    }
}
