package com.sprint.monew.common.batch.articlecollect.config;

import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.ARTICLE_IDS;

import com.sprint.monew.common.batch.support.ArticleInterestJdbc;
import com.sprint.monew.common.batch.support.ArticleWithInterestList;
import com.sprint.monew.common.batch.support.Interests;
import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.api.ArticleApiDto;
import com.sprint.monew.domain.article.articleinterest.ArticleInterest;
import jakarta.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ArticleChunkConfig {

  private final EntityManagerFactory emf;
  private final DataSource dataSource;

  /**
   *  Reader
   */
  @Bean(name = "naverArticleCollectReader")
  @StepScope
  public ItemReader<ArticleApiDto> naverArticleCollectReader(
      @Value("#{JobExecutionContext['naverArticleDtos']}") List<ArticleApiDto> naverArticleDtos) {
    return new ListItemReader<>(naverArticleDtos);
  }

  @Bean(name = "chosunArticleCollectReader")
  @StepScope
  public ItemReader<Object> chosunArticleCollectReader() {
    return null;
  }

  /**
   * Processor
   */

  @Bean
  @StepScope
  public ItemProcessor<ArticleApiDto, ArticleWithInterestList> articleCollectProcessor(
      @Value("#{JobExecutionContext['interests']}") Interests interests) {
    return interests::toArticleWithRelevantInterests;
  }

  @Bean
  @StepScope
  public ItemProcessor<ArticleApiDto, ArticleWithInterestList> restoreArticleProcessor(
      @Value("#{JobExecutionContext['interests']}") Interests interests) {
    return item -> {
      if (interests.isDuplicateUrl(item)){
        return null;
      }
      return interests.toArticleWithRelevantInterests(item);
    };
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
          ArticleInterestJdbc articleInterestJdbc = ArticleInterestJdbc.create(articleWithId,
              interest);
          articleInterestsJdbc.add(articleInterestJdbc);
        });

        articlesWithId.add(articleWithId);
      }

      ExecutionContext stepContext = StepSynchronizationManager.getContext().getStepExecution()
          .getExecutionContext();

      List<UUID> articleIdList = articlesWithId.stream()
          .map(Article::getId).toList();

      stepContext.put(ARTICLE_IDS.getKey(), articleIdList);

      log.info("저장 될 Article size : {}", articlesWithId.size());
      log.info("저장 될 Article Interest size : {}", articleInterestsJdbc.size());

      articleJdbcItemWriter.write((Chunk<? extends Article>) articlesWithId);
      articleInterestJdbcItemWriter.write((Chunk<? extends ArticleInterestJdbc>) articleInterestsJdbc);
    };
  }

  @Bean
  @StepScope
  public JdbcBatchItemWriter<Article> articleJdbcItemWriter() {

    String articleInsertSql =
        "INSERT INTO articles (id, source, source_url, title, publish_date, summary, deleted) " +
            "VALUES (:id, :source, :sourceUrl, :title, :publishDate, :summary, :deleted)";

    return new JdbcBatchItemWriterBuilder<Article>()
        .dataSource(dataSource)
        .assertUpdates(false)
        .sql(articleInsertSql)
        .columnMapped()
        .build();
  }

  @Bean
  @StepScope
  public JdbcBatchItemWriter<ArticleInterestJdbc> articleInterestJdbcItemWriter() {

    String articleInterestInsertSql =
        "INSERT INTO article_interests (id, article_id, interest_id, created_at) " +
            "VALUES (:id, :articleId, :interestId, :createdAt)";

    return new JdbcBatchItemWriterBuilder<ArticleInterestJdbc>()
        .dataSource(dataSource)
        .assertUpdates(false)
        .sql(articleInterestInsertSql)
        .columnMapped()
        .build();
  }

  /**
   *  jpaWrriter를 사용할 경우
   */

  @Bean
  @StepScope
  public ItemWriter<ArticleWithInterestList> articleJpaItemWriter() {

    return items -> {
      List<ArticleWithInterestList> articleAndInterestsList = (List<ArticleWithInterestList>) items.getItems();

      List<Article> articleList = articleAndInterestsList.stream()
          .map(ArticleWithInterestList::toArticle)
          .toList();

      List<ArticleInterest> articleInterestList = articleAndInterestsList.stream()
          .map(ArticleWithInterestList::toArticleInterests)
          .flatMap(List::stream)
          .toList();

      log.info("저장 될 Article: {}", articleList);
      log.info("저장 될 Article Interest : {}", articleInterestList);

      articleWriter().write((Chunk<? extends Article>) articleList);
      articleInterestWriter().write((Chunk<? extends ArticleInterest>) articleInterestList);
    };
  }

  private JpaItemWriter<Article> articleWriter() {
    return new JpaItemWriterBuilder<Article>()
        .usePersist(true)
        .entityManagerFactory(emf)
        .build();
  }

  private JpaItemWriter<ArticleInterest> articleInterestWriter() {
    return new JpaItemWriterBuilder<ArticleInterest>()
        .usePersist(true)
        .entityManagerFactory(emf)
        .build();
  }

}
