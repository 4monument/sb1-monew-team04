package com.sprint.monew.domain.comment.repository;

import static com.sprint.monew.domain.article.QArticle.article;
import static com.sprint.monew.domain.comment.QComment.comment;
import static com.sprint.monew.domain.user.QUser.user;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.monew.domain.comment.dto.CommentDto;
import com.sprint.monew.domain.comment.dto.request.CommentRequest;
import com.sprint.monew.domain.comment.like.QLike;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Slice<CommentDto> getComments(CommentRequest condition, UUID userId, Pageable pageable) {
    QLike likeAll = new QLike("likeAll");
    QLike likeMe = new QLike("likeMe");

    List<CommentDto> result = jpaQueryFactory
        .select(Projections.constructor(
            CommentDto.class, comment.id, article.id, user.id, user.nickname, comment.content,
            likeAll.count().intValue(), likeMe.id.isNotNull(), comment.createdAt
        ))
        .from(comment)
        .leftJoin(comment.article, article)
        .leftJoin(comment.user, user)
        .leftJoin(comment.likes, likeAll)
        .leftJoin(comment.likes, likeMe).on(likeMe.user.id.eq(userId))
        .where(
            articleIdEq(condition.articleId()),
            createdAtCursor(condition.cursor(), pageable.getSort())
        )
        .having(likeCountCursor(condition.cursor(), condition.after(), pageable.getSort(), likeAll))
        .groupBy(
            comment.id,
            article.id,
            user.id,
            user.nickname,
            comment.content,
            likeMe.id,
            comment.createdAt
        )
        .orderBy(getOrderSpecifiers(pageable.getSort(), likeAll))
        .limit(pageable.getPageSize() + 1)
        .fetch();

    boolean hasNext = result.size() > pageable.getPageSize();
    if (hasNext) {
      result.remove(result.size() - 1);
    }

    return new SliceImpl<>(result, pageable, hasNext);
  }

  private BooleanExpression articleIdEq(UUID articleId) {
    return article.id.eq(articleId);
  }

  private BooleanExpression createdAtCursor(String cursor, Sort sort) {
    if (cursor == null) {
      return null;
    }

    Sort.Order order = sort.iterator().next();
    String property = order.getProperty();

    if (!property.equals("createdAt")) {
      return null;
    }

    Instant createdAt = Instant.parse(cursor);
    return order.isAscending() ?
        comment.createdAt.gt(createdAt) :
        comment.createdAt.lt(createdAt);
  }

  private BooleanExpression likeCountCursor(String cursor, Instant after, Sort sort, QLike likeAll) {
    if (cursor == null || after == null) {
      return null;
    }

    Sort.Order order = sort.iterator().next();
    String property = order.getProperty();

    if (!property.equals("likeCount")) {
      return null;
    }

    Long likeCount = Long.parseLong(cursor);
    NumberExpression<Long> countExpr = likeAll.count();

    BooleanExpression primary = order.isAscending() ?
        countExpr.gt(likeCount) :
        countExpr.lt(likeCount);

    return primary.or(countExpr.eq(likeCount).and(comment.createdAt.lt(after)));
  }

  private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort, QLike likeAll) {
    List<OrderSpecifier<?>> orders = new ArrayList<>();
    Sort.Order order = sort.iterator().next();
    Order direction = order.isAscending() ? Order.ASC : Order.DESC;
    String property = order.getProperty();

    if (property.equals("likeCount")) {
      orders.add(new OrderSpecifier<>(direction, likeAll.count()));
      orders.add(new OrderSpecifier<>(Order.DESC, comment.createdAt));
    } else {
      orders.add(new OrderSpecifier<>(direction, comment.createdAt));
    }
    return orders.toArray(new OrderSpecifier[0]);
  }
}
