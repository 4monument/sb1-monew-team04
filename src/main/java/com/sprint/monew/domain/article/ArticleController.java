package com.sprint.monew.domain.article;

import com.sprint.monew.common.config.api.ArticleApi;
import com.sprint.monew.common.util.CursorPageResponseDto;
import com.sprint.monew.domain.article.dto.ArticleDto;
import com.sprint.monew.domain.article.dto.ArticleRestoreResultDto;
import com.sprint.monew.domain.article.dto.ArticleViewDto;
import com.sprint.monew.domain.article.dto.request.ArticleRequest;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController implements ArticleApi {

  private final ArticleService articleService;

  @PostMapping("/{articleId}/article-views")
  public ResponseEntity<ArticleViewDto> registerArticleView(
      @PathVariable UUID articleId,
      @RequestHeader("Monew-Request-User-ID") UUID userId
  ) {
    ArticleViewDto articleViewDto = articleService.registerArticleView(articleId, userId);
    return ResponseEntity.ok(articleViewDto);
  }

  @GetMapping
  public ResponseEntity<CursorPageResponseDto<ArticleDto>> getArticles(
      @ModelAttribute ArticleRequest articleRequest,
      @RequestParam String orderBy,
      @RequestParam String direction,
      @RequestParam int limit,
      @RequestHeader("Monew-Request-User-ID") UUID userId
  ) {
    PageRequest pageRequest = PageRequest.of(0, limit, Direction.fromString(direction), orderBy);
    CursorPageResponseDto<ArticleDto> response = articleService.getArticles(articleRequest,
        pageRequest, userId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/restore")
  public ResponseEntity<List<ArticleRestoreResultDto>> restoreArticles(
      @RequestParam Instant from,
      @RequestParam Instant to
  )
      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

    List<ArticleRestoreResultDto> response = articleService.restoreArticle(from,
        to);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteArticle(@PathVariable UUID id) {
    articleService.deleteArticle(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}/hard")
  public ResponseEntity<Void> hardDeleteArticle(@PathVariable UUID id) {
    articleService.hardDeleteArticle(id);
    return ResponseEntity.noContent().build();
  }

  //todo - 출처 목록 조회
}
