package com.sprint.monew.domain.article.repository;

import com.sprint.monew.domain.article.Article;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ArticleRepository extends JpaRepository<Article, UUID>, QuerydslPredicateExecutor<Article>, ArticleRepositoryCustom {

  Optional<Article> findByIdAndDeletedFalse(UUID id);

  List<Article> findAllByDeletedFalse();
}
