package com.sprint.monew.domain.interest;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.monew.domain.interest.dto.InterestSubscriptionInfoDto;
import com.sprint.monew.domain.interest.subscription.QSubscription;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomInterestRepositoryImpl implements CustomInterestRepository {

  private final JPAQueryFactory queryFactory;
  private final QInterest interest = QInterest.interest;
  private final QSubscription subscription = QSubscription.subscription;

  @Override
  public List<InterestSubscriptionInfoDto> getByNameOrKeywordsContaining(String keyword,
      UUID cursorId, Instant afterAt, String sortDirection, String sortField, Pageable pageable) {

    BooleanBuilder whereClause = new BooleanBuilder();

    // 검색어 조건
    applySearchCondition(whereClause, keyword);

    // 커서 조건
    applyCursorCondition(whereClause, cursorId, afterAt, sortDirection, sortField);

    // 정렬 조건
    OrderSpecifier<?>[] orderSpecifiers = getOrderSpecifiers(sortField, sortDirection);

    // 메인 쿼리 실행
    return queryFactory
        .select(Projections.constructor(InterestSubscriptionInfoDto.class,
            interest,
            subscription.user.countDistinct()))
        .from(interest)
        .leftJoin(subscription).on(subscription.interest.eq(interest))
        .where(whereClause)
        .groupBy(interest)
        .orderBy(orderSpecifiers)
        .limit(pageable.getPageSize())
        .fetch();
  }

  //검색어 조건 적용
  private void applySearchCondition(BooleanBuilder whereClause, String keyword) {
    if (keyword == null || keyword.isEmpty()) {
      return;
    }

    String likePattern = "%" + keyword.toLowerCase() + "%";

    BooleanExpression nameCondition = interest.name.lower().like(likePattern);
    BooleanExpression keywordCondition = Expressions.stringTemplate(
            "CAST({0} AS text)", interest.keywords)
        .toLowerCase()
        .like(likePattern);

    whereClause.and(nameCondition.or(keywordCondition));
  }

  //커서 조건 적용
  private void applyCursorCondition(BooleanBuilder whereClause, UUID cursorId,
      Instant afterAt, String sortDirection, String sortField) {
    if (cursorId == null || afterAt == null) {
      return;
    }

    Interest cursorInterest = getCursorInterest(cursorId);
    if (cursorInterest == null) {
      return;
    }

    if ("name".equalsIgnoreCase(sortField)) {
      whereClause.and(buildNameCursorCondition(cursorInterest, afterAt, sortDirection));
    } else {
      Long cursorSubscriberCount = getCursorSubscriberCount(cursorId);
      whereClause.and(buildSubscriberCountCursorCondition(cursorInterest,
          cursorSubscriberCount,
          afterAt,
          sortDirection));
    }
  }

  //커서 interest 객체 조회하기
  private Interest getCursorInterest(UUID cursorId) {
    return queryFactory
        .selectFrom(interest)
        .where(interest.id.eq(cursorId))
        .fetchOne();
  }

  //커서에 해당하는 구독자 수 구하기
  private Long getCursorSubscriberCount(UUID cursorId) {
    Long count = queryFactory
        .select(subscription.user.countDistinct())
        .from(subscription)
        .where(subscription.interest.id.eq(cursorId))
        .groupBy(subscription.interest.id)
        .fetchOne();

    return count != null ? count : 0L;
  }

  // 구한 구독자 수 기준 커서 조건을 만든다
  private BooleanBuilder buildSubscriberCountCursorCondition(
      Interest cursorInterest,
      Long cursorSubscriberCount,
      Instant afterAt,
      String sortDirection) {

    boolean isDesc = sortDirection.equalsIgnoreCase("desc");
    BooleanBuilder builder = new BooleanBuilder();

    // interest 테이블의 ID를 기준으로 join하여 찾은 subscription의 user 수 집계
    JPQLQuery<Long> subCountForInterest = JPAExpressions
        .select(subscription.user.countDistinct())
        .from(subscription)
        .where(subscription.interest.id.eq(interest.id));

    if (isDesc) {
      // 구독자 수가 커서보다 작을 때
      builder.or(subCountForInterest.lt(cursorSubscriberCount));

      // 구독자 수가 같고 생성일이 커서보다 이전일 때
      builder.or(subCountForInterest.eq(cursorSubscriberCount)
          .and(interest.createdAt.lt(afterAt)));

      // 구독자 수가 같고 생성일도 같고 ID가 커서보다 작을 때
      builder.or(subCountForInterest.eq(cursorSubscriberCount)
          .and(interest.createdAt.eq(afterAt))
          .and(interest.id.lt(cursorInterest.getId())));
    } else {
      builder.or(subCountForInterest.gt(cursorSubscriberCount));

      builder.or(subCountForInterest.eq(cursorSubscriberCount)
          .and(interest.createdAt.gt(afterAt)));

      builder.or(subCountForInterest.eq(cursorSubscriberCount)
          .and(interest.createdAt.eq(afterAt))
          .and(interest.id.gt(cursorInterest.getId())));
    }

    return builder;
  }

  //이름 기준으로 커서 조건 생성
  private BooleanExpression buildNameCursorCondition(
      Interest cursorInterest,
      Instant afterAt,
      String sortDirection) {

    boolean isDesc = sortDirection.equalsIgnoreCase("desc");
    String cursorName = cursorInterest.getName();

    if (isDesc) {
      // 내림차순:
      // name < cursorName OR
      // (name = cursorName AND createdAt < afterAt) OR
      // (name = cursorName AND createdAt = afterAt AND id < cursorId)
      return interest.name.lt(cursorName)
          .or(interest.name.eq(cursorName)
              .and(interest.createdAt.lt(afterAt)
                  .or(interest.createdAt.eq(afterAt)
                      .and(interest.id.lt(cursorInterest.getId())))));
    } else {
      // 오름차순:
      // name > cursorName OR
      // (name = cursorName AND createdAt > afterAt) OR
      // (name = cursorName AND createdAt = afterAt AND id > cursorId)
      return interest.name.gt(cursorName)
          .or(interest.name.eq(cursorName)
              .and(interest.createdAt.gt(afterAt)
                  .or(interest.createdAt.eq(afterAt)
                      .and(interest.id.gt(cursorInterest.getId())))));
    }
  }

  //정렬 조건 만들기
  private OrderSpecifier<?>[] getOrderSpecifiers(String sortField, String direction) {
    Order order = (direction != null && direction.equalsIgnoreCase("asc")) ? Order.ASC : Order.DESC;
    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

    // 관심사 이름으로 정렬
    if ("name".equalsIgnoreCase(sortField)) {
      orderSpecifiers.add(new OrderSpecifier<>(order, interest.name));
      orderSpecifiers.add(new OrderSpecifier<>(order, interest.createdAt));
      orderSpecifiers.add(new OrderSpecifier<>(order, interest.id));
      return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }
    // 구독자 수로 정렬(기본)
    orderSpecifiers.add(new OrderSpecifier<>(order, subscription.user.countDistinct()));
    orderSpecifiers.add(new OrderSpecifier<>(order, interest.createdAt));
    orderSpecifiers.add(new OrderSpecifier<>(order, interest.id));
    return orderSpecifiers.toArray(new OrderSpecifier[0]);
  }

//  public List<String> findAllKeyword() {
//    List<List<String>> nestedKeywordList = queryFactory.select(interest.keywords)
//        .from(interest)
//        .fetch();
//
//    List<String> unNestedKeywordList = nestedKeywordList.stream()
//        .flatMap(List::stream)
//        .distinct().toList();
//
//    return null;
//  }


}
