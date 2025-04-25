package com.sprint.monew.common.batch.util;


import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.api.ArticleApiDto;
import com.sprint.monew.domain.interest.Interest;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Interests {

  //private final Map<Article, Interest> articleInterestsMap;
  private final List<Interest> interests;
  private final List<String> keywords;

  public Interests(List<Interest> interests) {
    this.interests = Collections.unmodifiableList(interests);
    this.keywords = interests.stream()
        .map(Interest::getKeywords)
        .flatMap(List::stream)
        .distinct().toList();
  }

//  public List<ArticleApiDto> filterByKeywords(List<ArticleApiDto> articleApiDtos) {
//
//    articleApiDtos.stream()
//        .map(articleApiDto -> {
//          if (isContainsIn(articleApiDto)) {
//            return null;
//          }
//          return articleApiDtos;
//        })
//        //.filter(this::isContainsIn)
//        .toList();
//
//    return null;
//  }

//  public boolean isContainsIn(ArticleApiDto articleApiDto) {
//    String summary = articleApiDto.summary();
//    //summary.toLowerCase();
//    return keywords.stream()
//        .anyMatch(summary::contains);
//  }

  //
  public Map<Article, List<Interest>> mapToArticleInterestsMap(List<Article> articles) {
    return articles.stream()
        .collect(Collectors.toMap(
            article -> article,
            article -> interests.stream()
                .filter(
                    interest -> interest.isContainsKeyword(article)) // 해당 뉴스에 해당하지 않는 Keyword 제외시키기
                .toList()
        ));
  }

  // keyword랑 주제가 먼 ArticleAPiDto 필터링
  public List<ArticleApiDto> filterByKeywordAndUniqueUrl(List<ArticleApiDto> articleApiDtos) {
    Set<String> sourceUrlFilter = new HashSet<>(); // sourceUrl 중복 검사요
    return articleApiDtos.stream()
        .filter(this::isContainsIn)
        .filter(dto -> sourceUrlFilter.add(dto.sourceUrl()))
        .toList();
  }

  private Boolean isContainsIn(ArticleApiDto articleApiDto) {
    String summary = articleApiDto.summary();
    return keywords.stream()
        .anyMatch(summary::contains);
  }
}
