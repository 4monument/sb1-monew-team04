package com.sprint.monew.common.batch.articlecollect;

import com.sprint.monew.domain.article.Article;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class ArticleJpaItemWriterConfig {

  private final EntityManagerFactory emf;

  @Bean(name = "naverArticleCollectWriter")
  public ItemWriter<Article> naverArticleCollectWriter(List<? extends Article> items) {
    return new JpaItemWriterBuilder<Article>()
        .usePersist(true)
        .entityManagerFactory(emf)
        .build();
  }

}
