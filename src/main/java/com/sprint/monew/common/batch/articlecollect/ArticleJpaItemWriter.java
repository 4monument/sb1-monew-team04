package com.sprint.monew.common.batch.articlecollect;

import com.sprint.monew.common.batch.util.ArticlesAndArticleInterestsDTO;
import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.articleinterest.ArticleInterest;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@StepScope
public class ArticleJpaItemWriter implements ItemWriter<ArticlesAndArticleInterestsDTO> {

  private final EntityManagerFactory emf;

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

  @Override
  public void write(Chunk<? extends ArticlesAndArticleInterestsDTO> items) throws Exception {
    JpaItemWriter<Article> articleJpaItemWriter = articleWriter();
    JpaItemWriter<ArticleInterest> articleInterestJpaItemWriter = articleInterestWriter();

    List<Article> articleList = items.getItems().get(0).articleList();
    List<ArticleInterest> articleInterestList = items.getItems().get(0).articleInterests();
    // 여기서도 멀 스 적용?
    articleJpaItemWriter.write((Chunk<? extends Article>) articleList);
    articleInterestJpaItemWriter.write((Chunk<? extends ArticleInterest>) articleInterestList);
  }
}
