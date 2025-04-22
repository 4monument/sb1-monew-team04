package com.sprint.monew.domain.comment;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

  void deleteByArticle_Id(UUID articleId);

  List<Comment> findByArticle_Id(UUID articleId);
}
