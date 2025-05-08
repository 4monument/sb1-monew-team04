package com.sprint.monew.domain.comment;

import com.sprint.monew.common.util.CursorPageResponseDto;
import com.sprint.monew.domain.activity.UserActivityService;
import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.exception.ArticleNotFoundException;
import com.sprint.monew.domain.article.repository.ArticleRepository;
import com.sprint.monew.domain.comment.dto.CommentDto;
import com.sprint.monew.domain.comment.dto.CommentLikeDto;
import com.sprint.monew.domain.comment.dto.request.CommentRegisterRequest;
import com.sprint.monew.domain.comment.dto.CommentCondition;
import com.sprint.monew.domain.comment.dto.request.CommentUpdateRequest;
import com.sprint.monew.domain.comment.exception.CommentNotFoundException;
import com.sprint.monew.domain.comment.exception.CommentNotOwnedException;
import com.sprint.monew.domain.comment.exception.LikeAlreadyExistException;
import com.sprint.monew.domain.comment.like.Like;
import com.sprint.monew.domain.comment.like.LikeRepository;
import com.sprint.monew.domain.comment.repository.CommentRepository;
import com.sprint.monew.domain.notification.Notification;
import com.sprint.monew.domain.notification.NotificationRepository;
import com.sprint.monew.domain.notification.ResourceType;
import com.sprint.monew.domain.user.User;
import com.sprint.monew.domain.user.UserRepository;
import com.sprint.monew.domain.user.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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
  private final UserActivityService userActivityService;

  //댓글 조회 메서드
  public CursorPageResponseDto<CommentDto> getComments(CommentCondition request, UUID userId, Pageable pageable) {
    Slice<CommentDto> page = commentRepository.getComments(request, userId, pageable);
    long totalElement = commentRepository.countByArticle_Id(request.articleId());

    List<CommentDto> content = page.getContent();
    Sort sort = page.getSort();
    Object nextCursor = null;
    Instant after = null;

    if (!content.isEmpty()) {
      String property = sort.iterator().next().getProperty();
      if (property.equals("createdAt")) {
        nextCursor = content.get(content.size() - 1).createdAt();
      } else if (property.equals("likeCount")) {
        nextCursor = content.get(content.size() - 1).likeCount();
      }
      after = content.get(content.size() - 1).createdAt();
    }

    return new CursorPageResponseDto<>(
        page.getContent(),
        nextCursor,
        after,
        page.getSize(),
        totalElement,
        page.hasNext()
    );
  }

  //댓글 생성 메서드
  public CommentDto registerComment(CommentRegisterRequest request) {
    UUID articleId = request.articleId();
    UUID userId = request.userId();

    User user = userRepository.findByIdAndDeletedFalse(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));
    Article article = articleRepository.findByIdAndDeletedFalse(articleId)
        .orElseThrow(() -> ArticleNotFoundException.withId(articleId));

    Comment comment = Comment.create(user, article, request.content());
    commentRepository.save(comment);

    // 활동 내역 저장
    userActivityService.updateUserActivity(userId);

    return CommentDto.from(comment, false);
  }

  //좋아요 생성 메서드
  public CommentLikeDto commentLike(UUID commentId, UUID userId) {
    Comment comment = commentRepository.findByIdAndDeletedFalse(commentId)
        .orElseThrow(() -> CommentNotFoundException.withId(commentId));
    User user = userRepository.findByIdAndDeletedFalse(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    //좋아요를 이미 누른 상태면 예외 발생
    if (likeRepository.existsByCommentAndUser(comment, user)) {
      throw LikeAlreadyExistException.withCommentIdAndUserId(commentId, userId);
    }

    Like like = new Like(user, comment);
    likeRepository.save(like);

    // 알림 생성 후 저장
    String notificationMessage = user.getNickname() + "님이 나의 댓글을 좋아합니다.";
    Notification notification = new Notification(
        comment.getUser(),
        commentId,
        ResourceType.COMMENT,
        notificationMessage
    );
    notificationRepository.save(notification);

    //활동 내역 저장
    userActivityService.updateUserActivity(userId);

    long commentLikeCount = likeRepository.countByComment(comment);
    return CommentLikeDto.from(like, commentLikeCount);
  }

  //좋아요 취소 메서드
  public void unlikeComment(UUID commentId, UUID userId) {
    likeRepository.findByComment_IdAndUser_Id(commentId, userId).ifPresent(likeRepository::delete);
  }

  //댓글 내용 수정 메서드
  public CommentDto updateCommentContent(UUID commentId, UUID userId, CommentUpdateRequest commentUpdateRequest) {
    String content = commentUpdateRequest.content();
    Comment comment = commentRepository.findByIdAndDeletedFalse(commentId)
        .orElseThrow(() -> CommentNotFoundException.withId(commentId));
    User user = comment.getUser();

    //댓글 쓴 사용자가 일치하지 않으면 예외 발생
    if (!user.getId().equals(userId)) {
      throw CommentNotOwnedException.withCommentIdAndUserId(commentId, userId);
    }

    comment.updateContent(content);
    boolean likedByMe = likeRepository.existsByCommentAndUser(comment, user);

    return CommentDto.from(comment, likedByMe);
  }

  //댓글 논리 삭제
  public void deleteComment(UUID commentId) {
    commentRepository.findById(commentId).ifPresent(Comment::logicallyDelete);
  }

  //댓글 물리 삭제
  public void hardDeleteComment(UUID commentId) {
    commentRepository.findById(commentId).ifPresent(commentRepository::delete);
  }
}
