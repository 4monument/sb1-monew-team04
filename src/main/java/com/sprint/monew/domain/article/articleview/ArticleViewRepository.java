package com.sprint.monew.domain.article.articleview;

import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.user.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleViewRepository extends JpaRepository<ArticleView, UUID> {

  boolean existsByUserAndArticle(User user, Article article);

  long countByArticle(Article article);
}
