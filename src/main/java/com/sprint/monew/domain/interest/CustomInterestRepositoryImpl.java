package com.sprint.monew.domain.interest;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.monew.domain.interest.dto.InterestSubscriptionInfoDto;
import com.sprint.monew.domain.interest.subscription.QSubscription;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomInterestRepositoryImpl implements CustomInterestRepository {

  //얘를 통해서 queryDSL이 요청을 보낸다.
  private final JPAQueryFactory queryFactory;

  @Override
  public List<InterestSubscriptionInfoDto> getByNameOrKeywordsContaining(String keyword,
      UUID cursorId, Instant afterAt, String sortDirection, String sortField, Pageable pageable) {

    QInterest interest = QInterest.interest;
    QSubscription subscription = QSubscription.subscription;

    BooleanBuilder whereClause = new BooleanBuilder();

    //검색어 조건
    if (keyword != null && !keyword.isEmpty()) {
      String likePattern = "%" + keyword + "%";
      whereClause.and(interest.name.likeIgnoreCase(likePattern)
          .or(interest.keywords.any().likeIgnoreCase(likePattern))); // 리스트 내 부분 문자열 검색
    }

    // 커서 조건
    if (cursorId != null && afterAt != null) {
      whereClause.and(
          buildCursorCondition(interest, subscription, cursorId, afterAt, sortDirection));
    }

    // 정렬 조건
    OrderSpecifier<?>[] orderSpecifiers = getOrderSpecifiers(interest, subscription, sortField,
        sortDirection);

    return queryFactory
        .select(Projections.constructor(InterestSubscriptionInfoDto.class,
            interest,
            subscription.user.countDistinct())) //유저를 count
        .from(interest)
        .where(whereClause) //검색어 조건 + 커서 조건
        .leftJoin(subscription).on(subscription.interest.eq(interest))//관심사로 조인
        .groupBy(interest) // 관심사 별로 유저를 count 하기 위한 groupBy
        .orderBy(orderSpecifiers) //정렬 조건
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();
  }

  //커서 조건
  private BooleanBuilder buildCursorCondition(QInterest interest, QSubscription subscription,
      UUID cursorId, Instant afterAt, String sortDirection) {
    BooleanBuilder builder = new BooleanBuilder();

    if (cursorId == null) {
      return builder;
    }

    // 내림차순
    if (sortDirection.equalsIgnoreCase("desc")) {
      builder.and(interest.createdAt.lt(afterAt)
          .or(interest.createdAt.eq(afterAt).and(interest.id.lt(cursorId))));
      return builder;
    }

    //오름차순 기본
    builder.and(interest.createdAt.gt(afterAt)
        .or(interest.createdAt.eq(afterAt).and(interest.id.gt(cursorId))));

    return builder;
  }

  private OrderSpecifier<?>[] getOrderSpecifiers(QInterest interest, QSubscription subscription,
      String sortField, String direction) {

    Order order = direction.equalsIgnoreCase("asc") ? Order.ASC : Order.DESC;

    // 기본 정렬은 name 기준으로
    OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(order, interest.name);

    //만약 정렬 속성이 구독자 수라면
    if ("subscriberCount".equalsIgnoreCase(sortField)) {
      orderSpecifier = new OrderSpecifier<>(order, subscription.user.countDistinct());
    }

    return new OrderSpecifier[]{orderSpecifier};
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
