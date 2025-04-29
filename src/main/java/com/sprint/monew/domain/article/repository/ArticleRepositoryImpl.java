package com.sprint.monew.domain.article.repository;

import static com.sprint.monew.domain.article.QArticle.article;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.QArticle;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<Article> findArticles(Pageable pageable) {

    return null;
  }

  public List<String> findAllSourceUrl() {
    return queryFactory
        .select(article.sourceUrl)
        .from(article)
        .fetch();
  }
}
