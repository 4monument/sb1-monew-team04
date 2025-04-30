package com.sprint.monew.domain.comment.repository;

import static com.sprint.monew.domain.article.QArticle.article;
import static com.sprint.monew.domain.comment.QComment.comment;
import static com.sprint.monew.domain.user.QUser.user;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.monew.domain.comment.Comment;
import com.sprint.monew.domain.comment.dto.CommentDto;
import com.sprint.monew.domain.comment.dto.request.CommentRequest;
import com.sprint.monew.domain.comment.like.QLike;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Page<CommentDto> getComments(CommentRequest condition, UUID userId, Pageable pageable) {
    QLike likeAll = new QLike("likeAll");
    QLike likeMe = new QLike("likeMe");
    List<CommentDto> result = jpaQueryFactory
        .select(Projections.constructor(
            CommentDto.class, comment.id, article.id, user.id, user.nickname, comment.content,
            likeAll.count().intValue(), likeMe.id.isNotNull(), comment.createdAt
        ))
        .from(comment)
        .leftJoin(comment.article, article).fetchJoin()
        .leftJoin(comment.user, user).fetchJoin()
        .leftJoin(comment.likes, likeAll)
        .leftJoin(comment.likes, likeMe).on(likeMe.user.id.eq(userId))
        .where(
            articleIdEq(condition.articleId()),
            cursorCondition(condition.cursor(), condition.after(), pageable.getSort(), likeAll)
        )
        .groupBy(comment.id, likeMe.id)
        .orderBy(getOrderSpecifiers(pageable.getSort()))
        .limit(pageable.getPageSize())
        .fetch();

    Long count = jpaQueryFactory
        .select(comment.count())
        .from(comment)
        .where(articleIdEq(condition.articleId()))
        .fetchOne();

    return new PageImpl<>(result, pageable, count);
  }

  private BooleanExpression articleIdEq(UUID articleId) {
    return article.id.eq(articleId);
  }

  private BooleanExpression cursorCondition(String cursor, Instant after, Sort sort, QLike likeAll) {
    if (cursor == null || after == null) {
      return null;
    }

    Sort.Order order = sort.iterator().next();
    String property = order.getProperty();
    Order direction = order.isAscending() ? Order.ASC : Order.DESC;

    if (property.equals("createdAt")) {
      Instant createdAt = Instant.parse(cursor);
      return direction == Order.ASC ?
          comment.createdAt.gt(createdAt) :
          comment.createdAt.lt(createdAt);
    } else if (property.equals("likeCount")) {
      Long likeCount = Long.parseLong(cursor);
      NumberExpression<Long> countExpr = likeAll.count();

      BooleanExpression primary = direction == Order.ASC
          ? countExpr.gt(likeCount)
          : countExpr.lt(likeCount);

      BooleanExpression secondary = direction == Order.ASC
          ? comment.createdAt.gt(after)
          : comment.createdAt.lt(after);

      return primary.or(countExpr.eq(likeCount).and(secondary));
    }
    return null;
  }

  private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
    List<OrderSpecifier<?>> orders = new ArrayList<>();

    for (Sort.Order order : sort) {
      Order direction = order.isAscending() ? Order.ASC : Order.DESC;
      PathBuilder<Comment> pathBuilder = new PathBuilder<>(comment.getType(), comment.getMetadata());
      orders.add(new OrderSpecifier<>(direction, pathBuilder.getString(order.getProperty())));
    }
    return orders.toArray(new OrderSpecifier[0]);
  }
}
