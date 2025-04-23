package com.sprint.monew.domain.comment;

import com.sprint.monew.domain.article.Article;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

  long countByArticleAndDeletedFalse(Article article);
}
