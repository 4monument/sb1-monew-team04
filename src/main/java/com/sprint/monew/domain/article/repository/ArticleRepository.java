package com.sprint.monew.domain.article.repository;

import com.sprint.monew.domain.article.Article;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article, UUID>,
    QuerydslPredicateExecutor<Article>, ArticleRepositoryCustom {

  Optional<Article> findByIdAndDeletedFalse(UUID id);

  List<Article> findAllByDeletedFalse();

  @Modifying(clearAutomatically = true)
  @Query("""
         UPDATE Article as a
         SET a.deleted = false
         WHERE a.publishDate BETWEEN :from AND :to
      """)
  int changeDeletedFalseByPublishDateBetween(@Param("from") Instant from, @Param("to") Instant to);
}
