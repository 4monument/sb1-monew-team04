package com.sprint.monew.domain.article;

import com.sprint.monew.domain.article.dto.ArticleRestoreResultDto;
import com.sprint.monew.domain.article.dto.ArticleViewDto;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ArticleService {

  private final ArticleRepository articleRepository;

  public ArticleViewDto registerArticleView(UUID id, UUID userId) {
    return null;
  }

//  public CursorPageResponse<ArticleDto> getArticles(
//      ArticleRequest articleRequest, Pageable pageable) {
//    return null;
//  }

  public List<ArticleRestoreResultDto> restoreArticle(Instant from, Instant to) {
    return null;
  }

  public void deleteArticle(UUID id) {

  }

  public void hardDeleteArticle(UUID id) {

  }



}
