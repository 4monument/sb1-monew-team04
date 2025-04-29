package com.sprint.monew.domain.article;

import com.sprint.monew.common.util.CursorPageResponseDto;
import com.sprint.monew.domain.article.dto.ArticleDto;
import com.sprint.monew.domain.article.dto.ArticleRestoreResultDto;
import com.sprint.monew.domain.article.dto.ArticleViewDto;
import com.sprint.monew.domain.article.dto.request.ArticleRequest;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
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
public class ArticleController {

  private final ArticleService articleService;
  private final JobLauncher jobLauncher;

  @Resource(name = "articleRestoreJob")
  private final Job articleRestoreJob;

  @PostMapping("/{id}/article-views")
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

    JobParameters jobParameters = new JobParametersBuilder()
        .addLocalDate("backupDay", LocalDate.now()) // 똑같은 날짜 기간 복구는 하루에 한번만(무분별 방지)
        .addString("from", from.toString())
        .addString("to", to.toString())
        .toJobParameters();

    JobExecution jobExecution = jobLauncher.run(articleRestoreJob, jobParameters);
    // result 꺼내기
    //[
    //  {
    //    "restoreDate": "2025-04-29T01:24:37.762Z",
    //    "restoredArticleIds": [
    //      "3fa85f64-5717-4562-b3fc-2c963f66afa6"
    //    ],
    //    "restoredArticleCount": 9007199254740991
    //  }
    return null;
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
}
