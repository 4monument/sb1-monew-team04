package com.sprint.monew.domain.comment;

import com.sprint.monew.domain.article.Article;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

  void deleteByArticle_Id(UUID articleId);

  List<Comment> findByArticle_Id(UUID articleId);

  long countByArticle(Article article);
}
