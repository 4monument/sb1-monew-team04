package com.sprint.monew.domain.interest;

import static com.sprint.monew.domain.interest.QInterest.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomInterestRepositoryImpl implements CustomInterestRepository {

  private final JPAQueryFactory queryFactory;

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
