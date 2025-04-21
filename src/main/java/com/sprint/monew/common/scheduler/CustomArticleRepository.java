package com.sprint.monew.common.scheduler;


import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomArticleRepository {

  private final JPAQueryFactory jpaQueryFactory;

  public void findArticlesByZeroOffset() {

  }
}
