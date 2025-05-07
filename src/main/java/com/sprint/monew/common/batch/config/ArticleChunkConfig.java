package com.sprint.monew.common.batch.config;

import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.ARTICLE_IDS;

import com.sprint.monew.common.batch.support.ArticleInterestJdbc;
import com.sprint.monew.common.batch.support.ArticleWithInterestList;
import com.sprint.monew.common.batch.support.InterestContainer;
import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.api.ArticleApiDto;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ArticleChunkConfig {

  private final DataSource dataSource;

  /**
   * Reader
   */
  @Bean
  @StepScope
  public ItemReader<ArticleApiDto> articleCollectionsReader(
      @Value("#{JobExecutionContext['naverArticleDtos']}") List<ArticleApiDto> naver,
      @Value("#{JobExecutionContext['chosunArticleDtos']}") List<ArticleApiDto> chosun,
      @Value("#{JobExecutionContext['hankyungArticleDtos']}") List<ArticleApiDto> hankyung) {

    List<ArticleApiDto> articleApiDtos = new ArrayList<>();

    if (naver != null) {
      articleApiDtos.addAll(naver);
    }

    if (chosun != null) {
      articleApiDtos.addAll(chosun);
    }

    if (hankyung != null) {
      articleApiDtos.addAll(hankyung);
    }

    return new ListItemReader<>(articleApiDtos);
  }

  /**
   * Processor
   */
  @Bean
  @StepScope
  public ItemProcessor<ArticleApiDto, ArticleWithInterestList> collectArticleProcessor(
      InterestContainer interests) {
    return interests::toArticleWithRelevantInterests;
  }

  @Bean
  @StepScope
  public ItemProcessor<ArticleApiDto, ArticleWithInterestList> restoreArticleProcessor(
      InterestContainer interests) {
    return interests::toArticleWithRelevantInterests;
  }

  /**
   * Writer
   */
  @Bean
  @StepScope
  public ItemWriter<ArticleWithInterestList> articleWithInterestsJdbcItemWriter(
      @Qualifier("articleJdbcItemWriter") JdbcBatchItemWriter<Article> articleJdbcItemWriter,
      @Qualifier("articleInterestJdbcItemWriter") JdbcBatchItemWriter<ArticleInterestJdbc> articleInterestJdbcItemWriter) {
    return items -> {
      List<ArticleWithInterestList> articleWithInterestLists = (List<ArticleWithInterestList>) items.getItems();

      List<Article> articlesWithId = new ArrayList<>();
      List<ArticleInterestJdbc> articleInterestsJdbc = new ArrayList<>();

      for (ArticleWithInterestList item : articleWithInterestLists) {
        Article articleWithId = item.toArticleWithId();

        item.interestList().forEach(interest -> {
          articleInterestsJdbc.add(
              ArticleInterestJdbc.create(articleWithId.getId(), interest.getId()));
        });

        articlesWithId.add(articleWithId);
      }

      ExecutionContext stepContext = Objects.requireNonNull(StepSynchronizationManager.getContext())
          .getStepExecution()
          .getExecutionContext();

      List<UUID> articleIdList = articlesWithId.stream()
          .map(Article::getId)
          .toList();

      stepContext.put(ARTICLE_IDS.getKey(), articleIdList);

      log.info("articleIdList : {}", articleIdList);
      log.info("articleInterestJdbc : {}", articleInterestsJdbc);

      Chunk<Article> chunkArticles = new Chunk<>();
      chunkArticles.addAll(articlesWithId);
      Chunk<ArticleInterestJdbc> chunkArticleInterests = new Chunk<>();
      chunkArticleInterests.addAll(articleInterestsJdbc);

      log.info("저장 될 Article size : {}", articlesWithId.size());
      log.info("저장 될 Article Interest size : {}", articleInterestsJdbc.size());

      articleJdbcItemWriter.write(chunkArticles);
      articleInterestJdbcItemWriter.write(chunkArticleInterests);
    };
  }


  @Bean
  @StepScope
  public JdbcBatchItemWriter<Article> articleJdbcItemWriter() {

    String articleInsertSql =
        "INSERT INTO articles (id, source, source_url, title, publish_date, summary, deleted) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    return new JdbcBatchItemWriterBuilder<Article>()
        .dataSource(dataSource)
        .assertUpdates(false)
        .sql(articleInsertSql)
        .itemPreparedStatementSetter((item, ps) -> {
          ps.setObject(1, item.getId());
          ps.setString(2, item.getSource().name());
          ps.setString(3, item.getSourceUrl());
          ps.setString(4, item.getTitle());
          ps.setTimestamp(5, Timestamp.from(item.getPublishDate()));
          ps.setString(6, item.getSummary());
          ps.setBoolean(7, false);
        })
        .beanMapped()
        .build();
  }

  @Bean
  @StepScope
  public JdbcBatchItemWriter<ArticleInterestJdbc> articleInterestJdbcItemWriter() {

    String articleInterestInsertSql =
        "INSERT INTO articles_interests "
            + "(id, article_id, interest_id, created_at) VALUES (?, ?, ?, ?)";

    return new JdbcBatchItemWriterBuilder<ArticleInterestJdbc>()
        .dataSource(dataSource)
        .assertUpdates(false)
        .sql(articleInterestInsertSql)
        .itemPreparedStatementSetter((item, ps) -> {
          ps.setObject(1, item.id());
          ps.setObject(2, item.articleId());
          ps.setObject(3, item.interestId());
          ps.setTimestamp(4, Timestamp.from(item.createdAt()));
        })
        .build();
  }
}

