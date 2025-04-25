package com.sprint.monew.common.batch.articlecollect;

import com.sprint.monew.common.batch.util.ArticlesAndArticleInterestsDTO;
import com.sprint.monew.common.batch.util.Interests;
import com.sprint.monew.common.batch.util.Keywords;
import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.api.ArticleApiDto;
import com.sprint.monew.domain.article.articleinterest.ArticleInterest;
import com.sprint.monew.domain.interest.Interest;
import java.util.List;
import java.util.Map;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArticleProcessorConfig {

  // 키워드 필터링
  // 원본 기사 링크 중복 없애기

  @Bean
  @StepScope
  public ItemProcessor<Object, ArticlesAndArticleInterestsDTO> naverArticleCollectProcessor(
      @Value("#{JobExecutionContext['naverArticleDtos']}") List<ArticleApiDto> articleApiDtos,
      @Value("#{JobExecutionContext['interests']}") Interests interests) {

    return (item) -> {
      if (interests == null) {
        // 변환 및 다른 도메인 생성 로직 넣기
        return null;
      }

      // 중복 되는 url 로직 + keyword 필터링
      List<ArticleApiDto> filteredArticleDtos = interests.filterByKeywordAndUniqueUrl(
          articleApiDtos);

      // DTO -> Article 엔티티
      List<Article> articleList = filteredArticleDtos.stream()
          .map(ArticleApiDto::toEntity)
          .toList();

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
      return new ArticlesAndArticleInterestsDTO(articleList, articleInterestList);
    };
  }

//  @Bean
//  @StepScope
//  public ItemProcessor<Object, List<Article>> chosunArticleCollectProcessor(
//      @Value("#{JobExecutionContext['chosunArticleDtos']}") List<ArticleApiDto> articleApiDtos,
//      @Value("#{JobExecutionContext['keywords']}") Keywords keywords) {
//
//    return (item) -> {
//      if (keywords == null) {
//        // 변환 로직 넣기
//        // ARTICLE 말고 ArticleView랑 알림 기능 ???
//        List<Article> articleList = articleApiDtos.stream()
//            .map(ArticleApiDto::toEntity)
//            .toList();
//
//        return articleList;
//      }
//
//      List<ArticleApiDto> filteredArticleDtos = keywords.filter(articleApiDtos);
//
//      // 변환로직
//      return null;
//    };
//  }


}
