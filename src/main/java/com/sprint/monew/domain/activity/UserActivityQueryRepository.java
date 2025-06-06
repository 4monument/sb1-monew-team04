package com.sprint.monew.domain.activity;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.monew.domain.article.QArticle;
import com.sprint.monew.domain.article.articleview.QArticleView;
import com.sprint.monew.domain.article.dto.ArticleViewDto;
import com.sprint.monew.domain.comment.QComment;
import com.sprint.monew.domain.comment.dto.CommentActivityDto;
import com.sprint.monew.domain.comment.dto.CommentLikeActivityDto;
import com.sprint.monew.domain.interest.QInterest;
import com.sprint.monew.domain.interest.subscription.QSubscription;
import com.sprint.monew.domain.interest.subscription.SubscriptionDto;
import com.sprint.monew.domain.comment.like.QLike;
import com.sprint.monew.domain.user.QUser;
import com.sprint.monew.domain.user.User;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UserActivityQueryRepository {

  private final JPAQueryFactory queryFactory;

  public UserActivityDto findUserActivity(UUID userId) {
    QUser user = QUser.user;
    QSubscription subscription = QSubscription.subscription;
    QInterest interest = QInterest.interest;
    QComment comment = QComment.comment;
    QLike likeSub = new QLike("likeSub");
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
    List<SubscriptionDto> subscriptions = queryFactory.select(Projections.constructor(
            SubscriptionDto.class,
            interest.id,
            interest.id,
            interest.name,
            interest.keywords,
            JPAExpressions.select(subscription.count())
                .from(subscription)
                .where(subscription.interest.id.eq(interest.id)),
            interest.createdAt))
        .from(subscription)
        .join(
            subscription.interest,
            interest)
        .where(subscription.user.id.eq(userId))
        .fetch();

    // 3. 작성한 댓글
    List<CommentActivityDto> comments = queryFactory.select(Projections.constructor(
            CommentActivityDto.class,
            comment.id,
            comment.article.id,
            comment.article.title,
            comment.user.id,
            comment.user.nickname,
            comment.content,
            comment.likes.size(),
            comment.createdAt))
        .from(comment)
        .where(comment.user.id.eq(userId))
        .orderBy(comment.createdAt.desc())
        .limit(10)
        .fetch();

    // 4. 좋아요한 댓글
    List<CommentLikeActivityDto> likedComments = queryFactory.select(Projections.constructor(
            CommentLikeActivityDto.class,
            likeSub.id,
            likeSub.createdAt,
            likeSub.comment.id,
            likeSub.comment.article.id,
            likeSub.comment.article.title,
            likeSub.comment.user.id,
            likeSub.comment.user.nickname,
            likeSub.comment.content,
            likeSub.comment.likes.size().longValue(),
            likeSub.comment.createdAt))
        .from(likeSub)
        .where(likeSub.user.id.eq(userId))
        .orderBy(likeSub.comment.createdAt.desc())
        .limit(10)
        .fetch();

    // 5. 본 기사 기록
    List<ArticleViewDto> viewedArticles = queryFactory.select(Projections.constructor(
            ArticleViewDto.class,
            articleView.user.id,
            articleView.user.id,
            articleView.createdAt,
            article.id,
            article.source.stringValue(),
            article.sourceUrl,
            article.title,
            article.publishDate,
            article.summary,
            JPAExpressions.select(comment.count())
                .from(comment)
                .where(comment.article.id.eq(article.id)),
            JPAExpressions.select(articleView.count())
                .from(articleView)
                .where(articleView.article.id.eq(article.id))))
        .from(articleView)
        .join(
            articleView.article,
            article)
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
        viewedArticles);
  }
}
