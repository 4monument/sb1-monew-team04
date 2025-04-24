package com.sprint.monew.common.batch;

import com.sprint.monew.domain.article.api.ArticleApiClient;
import com.sprint.monew.domain.interest.InterestRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
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
    List<String> allKeyword = interestRepository.findAllKeyword();
    //articleApiClient.getNaverArticle()

    return null;
  }
}
