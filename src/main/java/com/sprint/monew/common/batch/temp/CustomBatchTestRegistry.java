package com.sprint.monew.common.batch.temp;


import static com.sprint.monew.domain.article.QArticle.*;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

//나중에 필요할 떄 Querydsl로 바꿀 것 : 지금은 테스트용
@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomBatchTestRegistry {

  private final JPAQueryFactory jpaQueryFactory;

  public String getQuery() {
    //JpaQueryProvider jpaQueryProvider = new JpaQueryProvider();
    return null;
  }

  private BooleanExpression getDeletedFalse() {
    return article.deleted.eq(false);
  }
}
