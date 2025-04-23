package com.sprint.monew.domain.article.repository;

import com.sprint.monew.domain.article.Article;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID>, ArticleRepositoryCustom {

  Optional<Article> findByIdAndDeletedFalse(UUID id);
}
