//package com.sprint.monew.common.batch.articlecollect;
//
//import com.sprint.monew.common.batch.util.ArticlesAndArticleInterestsDTO;
//import com.sprint.monew.domain.article.Article;
//import com.sprint.monew.domain.article.articleinterest.ArticleInterest;
//import jakarta.persistence.EntityManagerFactory;
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.batch.item.ItemWriter;
//import org.springframework.batch.item.database.JpaItemWriter;
//import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//@StepScope
//public class Test implements ItemWriter<ArticlesAndArticleInterestsDTO> {
//
//  private final EntityManagerFactory emf;
//
//  private JpaItemWriter<Article> articleWriter() {
//    return new JpaItemWriterBuilder<Article>()
//        .usePersist(true)
//        .entityManagerFactory(emf)
//        .build();
//  }
//
//  private JpaItemWriter<ArticleInterest> articleInterestWriter() {
//    return new JpaItemWriterBuilder<ArticleInterest>()
//        .usePersist(true)
//        .entityManagerFactory(emf)
//        .build();
//  }
//
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
//}
