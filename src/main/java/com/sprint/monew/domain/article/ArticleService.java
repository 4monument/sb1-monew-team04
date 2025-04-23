package com.sprint.monew.domain.article;

import com.sprint.monew.common.util.CursorPageResponseDto;
import com.sprint.monew.domain.article.articleview.ArticleView;
import com.sprint.monew.domain.article.articleview.ArticleViewRepository;
import com.sprint.monew.domain.article.dto.ArticleDto;
import com.sprint.monew.domain.article.dto.ArticleRestoreResultDto;
import com.sprint.monew.domain.article.dto.ArticleViewDto;
import com.sprint.monew.domain.article.dto.request.ArticleRequest;
import com.sprint.monew.domain.article.repository.ArticleRepository;
import com.sprint.monew.domain.comment.CommentRepository;
import com.sprint.monew.domain.user.User;
import com.sprint.monew.domain.user.UserRepository;
import com.sprint.monew.global.error.ErrorCode;
import com.sprint.monew.global.error.exception.article.ArticleViewAlreadyExistException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ArticleService {

  private final ArticleRepository articleRepository;
  private final UserRepository userRepository;
  private final CommentRepository commentRepository;
  private final ArticleViewRepository articleViewRepository;

  public ArticleViewDto registerArticleView(UUID id, UUID userId) {
    User user = userRepository.findByIdAndDeletedFalse(userId)
        .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getMessage()));
    Article article = articleRepository.findByIdAndDeletedFalse(id)
        .orElseThrow(() -> new IllegalArgumentException(ErrorCode.ARTICLE_NOT_FOUND.getMessage()));

    if (articleViewRepository.existsByUserAndArticle(user, article)) {
      throw ArticleViewAlreadyExistException.withId(user.getId(), article.getId());
    }

    ArticleView articleView = ArticleView.create(user, article);
    articleViewRepository.save(articleView);
    long commentCount = commentRepository.countByArticleAndDeletedFalse(article);
    long viewCount = articleViewRepository.countByArticle(article);

    return ArticleViewDto.from(articleView, commentCount, viewCount);
  }

  @Transactional(readOnly = true)
  public CursorPageResponseDto<ArticleDto> getArticles(
      ArticleRequest articleRequest, Pageable pageable) {
    return null;
  }

  public List<ArticleRestoreResultDto> restoreArticle(Instant from, Instant to) {
    return null;
  }

  public void deleteArticle(UUID id) {
    articleRepository.findById(id).ifPresent(Article::logicallyDelete);
  }

  public void hardDeleteArticle(UUID id) {
    articleRepository.findById(id).ifPresent(articleRepository::delete);
  }



}
