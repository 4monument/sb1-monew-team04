package com.sprint.monew.common.batch;

import static com.sprint.monew.common.batch.util.CustomExecutionContextKeys.*;

import com.sprint.monew.common.batch.util.ExecutionContextFinder;
import com.sprint.monew.common.batch.util.Keywords;
import com.sprint.monew.domain.article.api.ArticleApiClient;
import com.sprint.monew.domain.article.api.ArticleApiDto;
import com.sprint.monew.domain.interest.InterestRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@StepScope
@RequiredArgsConstructor
public class NaverApiCall implements Tasklet {

  private final ArticleApiClient articleApiClient;
  private final InterestRepository interestRepository;

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    // 네이버 호출하는 로직있다고 가정
    List<ArticleApiDto> articleApiDtos = articleApiClient.getNaverArticle("d");

    // API로 호출한 뉴스를 배치 저장소에 저장
    ExecutionContext jobExecutionContext = ExecutionContextFinder.findJobExecutionContext(
        contribution);
    jobExecutionContext.put(NAVER_ARTICLE_DTOS.getKey(), articleApiDtos);
    return RepeatStatus.FINISHED;
  }
}
