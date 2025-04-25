package com.sprint.monew.domain.comment.like;

import com.sprint.monew.domain.comment.Comment;
import com.sprint.monew.domain.user.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, UUID> {

  boolean existsByCommentAndUser(Comment comment, User user);

  long countByComment(Comment comment);

  Optional<Like> findByComment_IdAndUser_Id(UUID commentId, UUID userId);
}
