package com.sprint.monew.domain.comment;

import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.exception.ArticleNotFoundException;
import com.sprint.monew.domain.article.repository.ArticleRepository;
import com.sprint.monew.domain.comment.dto.CommentDto;
import com.sprint.monew.domain.comment.dto.request.CommentRegisterRequest;
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



}
