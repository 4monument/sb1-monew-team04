package com.sprint.monew.common.batch.articlecollect;

import com.sprint.monew.common.batch.util.ArticlesAndArticleInterestsDTO;
import com.sprint.monew.common.batch.util.Interests;
import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.articleinterest.ArticleInterest;
import com.sprint.monew.domain.interest.Interest;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@StepScope
public class ArticleJpaItemWriter implements ItemWriter<Article> {

  private final EntityManagerFactory emf;
  private final Interests interests;


  public ArticleJpaItemWriter(EntityManagerFactory emf,
      @Value("#{JobExecutionContext['interests']}") Interests interests) {
    this.emf = emf;
    this.interests = interests;
  }

  @Override
  public void write(Chunk<? extends Article> items) throws Exception {

    List<Article> articleList = (List<Article>) items.getItems();

    // 각 Article 별 포함된 관심사
    Map<Article, List<Interest>> mappedToArticleInterestsMap = interests.mapToArticleInterestsMap(
        articleList);

    // ArticleInterest 객체 생성
    List<ArticleInterest> articleInterestList = mappedToArticleInterestsMap.keySet().stream()
        .map(article -> {
          List<Interest> relevantInterests = mappedToArticleInterestsMap.get(article);
          return relevantInterests.stream()
              .map(interest -> ArticleInterest.create(article, interest))
              .toList();
        })
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