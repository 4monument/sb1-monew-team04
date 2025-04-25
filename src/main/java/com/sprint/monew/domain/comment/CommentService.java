package com.sprint.monew.domain.comment;

import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.exception.ArticleNotFoundException;
import com.sprint.monew.domain.article.repository.ArticleRepository;
import com.sprint.monew.domain.comment.dto.CommentDto;
import com.sprint.monew.domain.comment.dto.CommentLikeDto;
import com.sprint.monew.domain.comment.dto.request.CommentRegisterRequest;
import com.sprint.monew.domain.comment.exception.CommentNotFoundException;
import com.sprint.monew.domain.comment.exception.LikeAlreadyExistException;
import com.sprint.monew.domain.comment.like.Like;
import com.sprint.monew.domain.comment.like.LikeRepository;
import com.sprint.monew.domain.notification.Notification;
import com.sprint.monew.domain.notification.NotificationRepository;
import com.sprint.monew.domain.notification.ResourceType;
import com.sprint.monew.domain.user.User;
import com.sprint.monew.domain.user.UserRepository;
import com.sprint.monew.domain.user.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

  private final CommentRepository commentRepository;
  private final UserRepository userRepository;
  private final ArticleRepository articleRepository;
  private final LikeRepository likeRepository;
  private final NotificationRepository notificationRepository;

  public CommentDto registerComment(CommentRegisterRequest request) {
    UUID articleId = request.articleId();
    UUID userId = request.userId();

    User user = userRepository.findByIdAndDeletedFalse(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));
    Article article = articleRepository.findByIdAndDeletedFalse(articleId)
        .orElseThrow(() -> ArticleNotFoundException.withId(articleId));

    Comment comment = Comment.create(user, article, request.content());
    commentRepository.save(comment);
    return CommentDto.from(comment, List.of(), false);
  }

  public CommentLikeDto commentLike(UUID commentId, UUID userId) {
    Comment comment = commentRepository.findByIdAndDeletedFalse(commentId)
        .orElseThrow(() -> CommentNotFoundException.withId(commentId));
    User user = userRepository.findByIdAndDeletedFalse(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    if (likeRepository.existsByCommentAndUser(comment, user)) {
      throw LikeAlreadyExistException.withCommentIdAndUserId(commentId, userId);
    }

    Like like = new Like(user, comment);
    likeRepository.save(like);

    Notification notification = new Notification(
        user,
        commentId,
        ResourceType.COMMENT,
        //알림 내용은 어떻게 하는게 좋을까요?
        "댓글이 등록되었습니다."
    );
    notificationRepository.save(notification);

    long commentLikeCount = likeRepository.countByComment(comment);
    return CommentLikeDto.from(like, commentLikeCount);
  }

  public void unlikeComment(UUID commentId, UUID userId) {
    likeRepository.findByComment_IdAndUser_Id(commentId, userId).ifPresent(likeRepository::delete);
  }

  public void deleteComment(UUID commentId) {
    commentRepository.findById(commentId).ifPresent(Comment::logicallyDelete);
  }

}
