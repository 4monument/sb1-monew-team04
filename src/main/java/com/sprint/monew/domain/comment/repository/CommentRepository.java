package com.sprint.monew.domain.comment.repository;

import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.comment.Comment;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID>, CommentRepositoryCustom {

  long countByArticleAndDeletedFalse(Article article);

  Optional<Comment> findByIdAndDeletedFalse(UUID id);

  long countByArticle_Id(UUID articleId);
}
