package com.sprint.monew.domain.article;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID> {

  Optional<Article> findByIdAndDeletedFalse(UUID id);
}
