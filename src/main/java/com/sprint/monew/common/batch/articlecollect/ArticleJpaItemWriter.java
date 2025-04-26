package com.sprint.monew.common.batch.articlecollect;

import com.sprint.monew.common.batch.util.ArticleWithInterestList;
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
import org.springframework.stereotype.Component;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class ArticleJpaItemWriter implements ItemWriter<ArticleWithInterestList> {

  private final EntityManagerFactory emf;

  @Override
  public void write(Chunk<? extends ArticleWithInterestList> items) {

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

//    JpaItemWriter<Article> articleJpaItemWriter = articleWriter();
//    JpaItemWriter<ArticleInterest> articleInterestJpaItemWriter = articleInterestWriter();
//
//    List<Article> articleList = items.getItems().get(0).articleList();
//    List<ArticleInterest> articleInterestList = items.getItems().get(0).articleInterests();
//    // 여기서도 멀 스 적용?
//    articleJpaItemWriter.write((Chunk<? extends Article>) articleList);
//    articleInterestJpaItemWriter.write((Chunk<? extends ArticleInterest>) articleInterestList);

//  @Override
//  public void write(List<? extends ArticlesAndArticleInterestsDTO> items) throws Exception {
//    // 여러 DTO가 한 청크에 묶여 있을 수 있으므로 flatten
//    List<Article> allArticles = items.stream()
//        .flatMap(dto -> dto.articleList().stream())
//        .toList();
//    List<ArticleInterest> allInterests = items.stream()
//        .flatMap(dto -> dto.articleInterests().stream())
//        .toList();
//
//    // 단순히 List를 넘겨준다
//    articleWriter().write(allArticles);
//    articleInterestWriter().write(allInterests);
//  }