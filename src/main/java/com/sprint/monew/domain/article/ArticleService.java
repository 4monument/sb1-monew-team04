package com.sprint.monew.domain.article;

import com.sprint.monew.common.util.CursorPageResponseDto;
import com.sprint.monew.domain.article.dto.ArticleDto;
import com.sprint.monew.domain.article.dto.ArticleRestoreResultDto;
import com.sprint.monew.domain.article.dto.ArticleViewDto;
import com.sprint.monew.domain.article.dto.request.ArticleRequest;
import com.sprint.monew.domain.comment.CommentRepository;
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
  private final CommentRepository commentRepository;

  public ArticleViewDto registerArticleView(UUID id, UUID userId) {
    return null;
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
