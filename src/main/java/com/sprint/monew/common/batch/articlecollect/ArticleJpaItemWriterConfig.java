package com.sprint.monew.common.batch.articlecollect;

import com.sprint.monew.common.batch.support.ArticleWithInterestList;
import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.articleinterest.ArticleInterest;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ArticleJpaItemWriterConfig {

  private final EntityManagerFactory emf;

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