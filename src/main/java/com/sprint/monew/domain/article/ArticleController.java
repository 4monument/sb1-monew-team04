package com.sprint.monew.domain.article;

import com.sprint.monew.domain.article.dto.ArticleRestoreResultDto;
import com.sprint.monew.domain.article.dto.ArticleViewDto;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
public class ArticleController {

  private final ArticleService articleService;

  @PostMapping("/{id}/article-views")
  public ResponseEntity<ArticleViewDto> registerArticleView(
      @PathVariable UUID articleId,
      @RequestHeader("Monew-Request-User-ID") UUID userId
      ) {
    ArticleViewDto articleViewDto = articleService.registerArticleView(articleId, userId);
    return ResponseEntity.ok(articleViewDto);
  }

  //CursorPageResponse가 아직 없어서 일단 주석으로 처리했습니다.
//  @GetMapping
//  public ResponseEntity<CursorPageResponse<ArticleDto>> getArticles(
//      @ModelAttribute ArticleRequest articleRequest,
//      @RequestParam String orderBy,
//      @RequestParam String direction,
//      @RequestParam int limit,
//      @RequestHeader("Monew-Request-User-ID") UUID userId
//  ) {
//    //임시 코드
//    PageRequest pageRequest = PageRequest.of(0, limit);
//    CursorPageResponse<ArticleDto> response = articleService.getArticles(articleRequest,
//        pageRequest);
//    return ResponseEntity.ok(response);
//  }

  @GetMapping("/restore")
  public ResponseEntity<List<ArticleRestoreResultDto>> restoreArticles(
      @RequestParam Instant from,
      @RequestParam Instant to
  ) {
    return null;
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteArticle(@PathVariable String id) {
    return null;
  }

  @DeleteMapping("/{id}/hard")
  public ResponseEntity<Void> hardDeleteArticle(@PathVariable String id) {
    return null;
  }
}
