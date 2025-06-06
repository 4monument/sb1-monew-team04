package com.sprint.monew.domain.notification.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.monew.domain.notification.Notification;
import com.sprint.monew.domain.notification.QNotification;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class NotificationRepositoryCustomImpl implements NotificationRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Notification> getUnconfirmedWithCursor(UUID userId, Instant cursor, Instant afterAt,
      Pageable pageable) {

    QNotification notification = QNotification.notification;

    BooleanBuilder whereClause = new BooleanBuilder();

    whereClause.and(notification.user.id.eq(userId)).and(notification.confirmed.isFalse());

    if (cursor != null && afterAt != null) {
      whereClause.and(notification.createdAt.lt(cursor)
          .or(notification.createdAt.lt(afterAt).and(notification.createdAt.lt(cursor))));
    }

    return queryFactory.select(notification)
        .from(notification)
        .where(whereClause)
        .orderBy(notification.createdAt.desc(), notification.id.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();
  }
}
