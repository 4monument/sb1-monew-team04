package com.sprint.monew.common.batch.articlecollect;

import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.CHOSUN_ARTICLE_DTOS;
import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.HANKYUNG_ARTICLE_DTOS;
import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.NAVER_ARTICLE_DTOS;

import com.sprint.monew.domain.article.api.ArticleApiClient;
import com.sprint.monew.domain.article.api.ArticleApiDto;
import com.sprint.monew.global.config.S3ConfigProperties;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@RequiredArgsConstructor
public class ArticleApiCallTaskletConfig {

  private final ArticleApiClient articleApiClient;
  private final S3Client s3Client;
  private final S3ConfigProperties s3Properties;

  @Bean
  @StepScope
  public Tasklet naverApiCallTasklet() {
    return (contribution, chunkContext) -> {
      List<ArticleApiDto> articleApiDtos = articleApiClient.getNaverArticle();
      ExecutionContext stepContext = contribution.getStepExecution().getExecutionContext();
      stepContext.put(NAVER_ARTICLE_DTOS.getKey(), articleApiDtos);
      return RepeatStatus.FINISHED;
    };
  }

  @Bean
  @StepScope
  public Tasklet chosunApiCallTasklet() {
    return (contribution, chunkContext) -> {
      List<ArticleApiDto> articleApiDtos = articleApiClient.getNaverArticle();
      ExecutionContext stepContext = contribution.getStepExecution().getExecutionContext();
      stepContext.put(CHOSUN_ARTICLE_DTOS.getKey(), articleApiDtos);
      return RepeatStatus.FINISHED;
    };
  }

  @Bean
  @StepScope
  public Tasklet hankyungApiCallTasklet() {
    return (contribution, chunkContext) -> {
      List<ArticleApiDto> articleApiDtos = articleApiClient.getNaverArticle();
      ExecutionContext stepContext = contribution.getStepExecution().getExecutionContext();
      stepContext.put(HANKYUNG_ARTICLE_DTOS.getKey(), articleApiDtos);
      return RepeatStatus.FINISHED;
    };
  }
  //String fileName = tempDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv";
}
