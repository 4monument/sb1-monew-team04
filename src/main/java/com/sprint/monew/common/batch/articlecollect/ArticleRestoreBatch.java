package com.sprint.monew.common.batch.articlecollect;

import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.INTERESTS;

import com.sprint.monew.common.batch.support.ArticleWithInterestList;
import com.sprint.monew.common.batch.support.Interests;
import com.sprint.monew.domain.article.Article.Source;
import com.sprint.monew.domain.article.api.ArticleApiDto;
import com.sprint.monew.domain.article.repository.ArticleRepository;
import com.sprint.monew.domain.interest.InterestRepository;
import com.sprint.monew.global.config.S3ConfigProperties;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.transaction.PlatformTransactionManager;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

@Configuration
@RequiredArgsConstructor
public class ArticleRestoreBatch {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final S3Client s3Client;
  private final S3ConfigProperties s3Properties;
  private final ArticleRepository articleRepository;

  @Bean
  public Job articleRestoreJob(
      @Qualifier("articleRestoreStep") Step articleRestoreStep,
      @Qualifier("interestsAndSourceUrlFetchStep") Step interestsAndSourceUrlFetchStep,
      @Qualifier("articleCollectJobContextCleanupListener") JobExecutionListener jobContextCleanupListener,
      @Qualifier("changeArticleIsDeletedStep") Step changeArticleIsDeletedStep) {

    return new JobBuilder("articleRestoreJob", jobRepository)
        .start(interestsAndSourceUrlFetchStep)  // 1. Interest 가져오기: ArticleInterest도 생성해야 하므로 Interest 객체 필요
        .next(changeArticleIsDeletedStep) // 2.  특정 날짜 기준 논리삭제된 것 전부 True
        .next(articleRestoreStep) // 3. 복구 : 기존에 있는거는 추가하지 않기
        .listener(jobContextCleanupListener)
        .build();
  }

  @Bean
  @JobScope
  public Step interestsAndSourceUrlFetchStep(InterestRepository interestRepository,
      @Qualifier("interestsFetchPromotionListener") ExecutionContextPromotionListener promotionListener) {

    return new StepBuilder("interestsFetchStep", jobRepository)
        .tasklet((contribution, chunkContext) -> {

          ExecutionContext stepContext = contribution.getStepExecution().getExecutionContext();
          Interests interests = new Interests(interestRepository.findAll());
          interests.addSourceUrls(articleRepository.findAllSourceUrl());
          stepContext.put(INTERESTS.getKey(), interests);

          return RepeatStatus.FINISHED;
        }, transactionManager)
        .listener(promotionListener)
        .build();
  }

  /**
  복구 요청 범위에서 -> 논리삭제 기사의 deleted 필드 변경
   **/
  @Bean
  @StepScope
  public Step changeArticleIsDeletedStep(
      @Value("#{jobParameters['from']}") String fromStr,
      @Value("#{jobParameters['to']}") String toStr) {

    return new StepBuilder("  changeArticleIsDeltedStep", jobRepository)
        .tasklet((contribution, chunkContext) -> {

          String from = getLocalDate(fromStr).toString();
          String to = getLocalDate(toStr).plusDays(1).toString();
          articleRepository.changeDeletedFalseByPublishDateBetween(from, to);

          return RepeatStatus.FINISHED;
        }, transactionManager)
        .build();
  }

  /**
   * Reader : S3에서 여러 파일리소프를 ArticleApiDto로 읽어오는 reader
   * processor : db에 저장되어있는 sourceUrl겹치는거 필터링 + 해당 기사의 관심사 매핑
   * wirter : 기사 + 기사 관심사 저장
   */
  @Bean
  @JobScope
  public Step articleRestoreStep(
      @Qualifier("articleRestoreS3ItemReader") MultiResourceItemReader<ArticleApiDto> mrir,
      @Qualifier("restoreArticleProcessor") ItemProcessor<ArticleApiDto, ArticleWithInterestList> restoreArticleProcessor,
      @Qualifier("articleWithInterestsJdbcItemWriter") ItemWriter<ArticleWithInterestList> articleJdbcItemWriter,
      @Qualifier("restoreArticleIdsPromotionListener") ExecutionContextPromotionListener restoreArticleIdsListener) {

    return new StepBuilder("articleRestoreStep", jobRepository)
        .<ArticleApiDto, ArticleWithInterestList>chunk(500, transactionManager)
        .reader(mrir)
        .processor(restoreArticleProcessor)
        .writer(articleJdbcItemWriter)
        .listener(restoreArticleIdsListener)
        .build();
  }

  // 일단 s3
  @Bean
  @StepScope
  public MultiResourceItemReader<ArticleApiDto> articleRestoreS3ItemReader(
      @Value("#{jobParameters['from']}") String fromStr,
      @Value("#{jobParameters['to']}") String toStr,
      @Qualifier("csvReader") ResourceAwareItemReaderItemStream<ArticleApiDto> csvReader) {

    LocalDate from = getLocalDate(fromStr);
    LocalDate to = getLocalDate(toStr);
    // 날짜 레인지
    List<LocalDate> dateRange = from.datesUntil(to.plusDays(1)).toList();

    List<S3Object> s3Objects = new ArrayList<>();
    for (LocalDate localDate : dateRange) {
      ListObjectsV2Request v2Request = ListObjectsV2Request.builder()
          .bucket(s3Properties.bucket())
          .prefix(localDate + "/")
          .encodingType("UTF-8")
          .build();

      ListObjectsV2Response v2Response = s3Client.listObjectsV2(v2Request);
      s3Objects.addAll(v2Response.contents());
    }

    List<Resource> resources = new ArrayList<>();
    for (S3Object s3Object : s3Objects) {

      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(s3Properties.bucket())
          .key(s3Object.key())
          .build();

      ResponseInputStream<GetObjectResponse> ri = s3Client.getObject(getObjectRequest); // 리소스 세는 곳
      resources.add(new InputStreamResource(ri));
    }

    Resource[] resourcesArray = resources.toArray(Resource[]::new);
    return new MultiResourceItemReaderBuilder<ArticleApiDto>()
        .resources(resourcesArray)
        .delegate(csvReader)
        .build();
  }

  private LocalDate getLocalDate(String fromStr) {
    Instant fromInstant = Instant.parse(fromStr);
    return fromInstant.atZone(ZoneId.systemDefault()).toLocalDate();
  }

  @Bean
  @StepScope
  public ResourceAwareItemReaderItemStream<ArticleApiDto> csvReader() {
    String[] fieldNames = new String[]{"source", "sourceUrl", "title", "publishDate", "summary"};
    return new FlatFileItemReaderBuilder<ArticleApiDto>()
        .delimited()
        .delimiter(",")
        .names(fieldNames)
        .fieldSetMapper(fs -> {
              Source source = Source.valueOf(fs.readString("source"));
              String sourceUrl = fs.readString("sourceUrl");
              String title = fs.readString("title");
              Instant publishAt = Instant.parse(fs.readString("publishDate"));
              String summary = fs.readString("summary");

              return ArticleApiDto.builder()
                  .source(source)
                  .sourceUrl(sourceUrl)
                  .title(title)
                  .publishDate(publishAt)
                  .summary(summary)
                  .build();
            }
        )
        .build();
  }
}
