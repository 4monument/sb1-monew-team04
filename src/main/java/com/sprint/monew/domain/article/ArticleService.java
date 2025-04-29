package com.sprint.monew.domain.article;

import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.*;

import com.sprint.monew.common.batch.support.CustomExecutionContextKeys;
import com.sprint.monew.common.util.CursorPageResponseDto;
import com.sprint.monew.domain.activity.UserActivityService;
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
import com.sprint.monew.domain.article.exception.ArticleViewAlreadyExistException;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.time.LocalDate;
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
import org.springframework.batch.item.ExecutionContext;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ArticleService {


  @Resource(name = "articleRestoreJob")
  private final Job articleRestoreJob;
  private final JobLauncher jobLauncher;
  private final ArticleRepository articleRepository;
  private final UserRepository userRepository;
  private final CommentRepository commentRepository;
  private final ArticleViewRepository articleViewRepository;
  private final UserActivityService userActivityService;

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

    userActivityService.updateUserActivity(userId);

    long commentCount = commentRepository.countByArticleAndDeletedFalse(article);
    long viewCount = articleViewRepository.countByArticle(article);

    return ArticleViewDto.from(articleView, commentCount, viewCount);
  }

  @Transactional(readOnly = true)
  public CursorPageResponseDto<ArticleDto> getArticles(
      ArticleRequest articleRequest, Pageable pageable, UUID userId) {
    //임시로 모든 article 반환
    List<ArticleDto> dtos = articleRepository.findAllByDeletedFalse().stream()
        .map(article -> {
          long commentCount = commentRepository.countByArticleAndDeletedFalse(article);
          long viewCount = articleViewRepository.countByArticle(article);
          return ArticleDto.from(article, commentCount, viewCount, true);
        })
        .toList();
    return new CursorPageResponseDto<>(
        dtos,
        null,
        null,
        dtos.size(),
        dtos.size(),
        false
    );
  }


  public List<ArticleRestoreResultDto> restoreArticle(Instant from, Instant to)
      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

    JobParameters jobParameters = new JobParametersBuilder()
        .addLocalDate("backupDay", LocalDate.now())
        .addString("from", from.toString())
        .addString("to", to.toString())
        .toJobParameters();

    JobExecution jobExecution = jobLauncher.run(articleRestoreJob, jobParameters);

    ExecutionContext jobContext = jobExecution.getExecutionContext();
    List<UUID> articleIds = (List<UUID>) jobContext.get(ARTICLE_IDS.getKey());
    if (articleIds == null || articleIds.isEmpty()) {
      throw new RuntimeException("ExecutionContext로부터 Article Ids를 가져오는 데 실패했습니다.");
    }

    ArticleRestoreResultDto result = new ArticleRestoreResultDto(
        Instant.now(),
        articleIds,
        (long) articleIds.size()
    );
    return List.of(result);
  }

  public void deleteArticle(UUID id) {
    articleRepository.findById(id).ifPresent(Article::logicallyDelete);
  }

  public void hardDeleteArticle(UUID id) {
    articleRepository.findById(id).ifPresent(articleRepository::delete);
  }


}
