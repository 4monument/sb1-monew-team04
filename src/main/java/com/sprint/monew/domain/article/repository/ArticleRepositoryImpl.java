package com.sprint.monew.domain.article.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.monew.domain.article.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepositoryCustom{

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<Article> findArticles(Pageable pageable) {



    return null;
  }
}
