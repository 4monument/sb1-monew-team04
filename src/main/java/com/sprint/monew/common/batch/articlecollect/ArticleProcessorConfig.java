package com.sprint.monew.common.batch.articlecollect;

import com.sprint.monew.common.batch.util.Interests;
import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.api.ArticleApiDto;
import java.util.List;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArticleProcessorConfig {

  @Bean
  @StepScope
  public ItemProcessor<ArticleApiDto, Article> naverArticleCollectProcessor(
      @Value("#{JobExecutionContext['interests']}") Interests interests) {
    return (dto) -> {
      // keyword 필터링 + 중복 되는 url 하나로
      if (interests.isDuplicateUrl(dto) || interests.isContainKeywords(dto)) {
        return null;
      }
      return dto.toEntity();
    };
  }

  //      // 각 Article 별 포함된 관심사
//      Map<Article, List<Interest>> mappedToArticleInterestsMap = interests.mapToArticleInterestsMap(
//          articleList);
//
//      // ArticleInterest 객체 생성
//      List<ArticleInterest> articleInterestList = mappedToArticleInterestsMap.keySet().stream()
//          .map(article -> {
//            List<Interest> relevantInterests = mappedToArticleInterestsMap.get(article);
//            return relevantInterests.stream()
//                .map(interest -> ArticleInterest.create(article, interest))
//                .toList();
//          })
//          .flatMap(List::stream)
//          .toList();
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
