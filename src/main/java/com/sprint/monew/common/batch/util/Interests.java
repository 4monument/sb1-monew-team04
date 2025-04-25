package com.sprint.monew.common.batch.util;


import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.api.ArticleApiDto;
import com.sprint.monew.domain.interest.Interest;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Interests implements Serializable {
  private static final long serialVersionUID = 1L;
  private final List<Interest> interests;
  private final List<String> keywords; // keyword 요청할때 마다 만드는 것보다 미리 만드는게 성능 좋을 것 같아서
  private final Set<String> sourceUrlFilterSet = ConcurrentHashMap.newKeySet();
  public Interests(List<Interest> interests) {
    this.interests = Collections.unmodifiableList(interests);
    this.keywords = interests.stream()
        .map(Interest::getKeywords)
        .flatMap(List::stream)
        .distinct().toList();
  }

  public Boolean validateKeywordContainingAndUniqueUrl(ArticleApiDto articleApiDto) {
    return isContainKeywords(articleApiDto) && isDuplicateUrl(articleApiDto);
  }

  public Boolean isContainKeywords(ArticleApiDto articleApiDto) {
    String summary = articleApiDto.summary();
    return keywords.stream()
        .anyMatch(summary::contains);
  }

  public boolean isDuplicateUrl(ArticleApiDto articleApiDto) {
    return sourceUrlFilterSet.add(articleApiDto.sourceUrl());
  }


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

  //  // keyword랑 주제가 먼 ArticleAPiDto 필터링 + URL 겹치는거
//  public List<ArticleApiDto> filterByKeywordAndUniqueUrl(List<ArticleApiDto> articleApiDtos) {
//    Set<String> sourceUrlFilter = new HashSet<>(); // sourceUrl 중복 검사요
//    return articleApiDtos.stream()
//        .filter(this::isContainKeywords)
//        .filter(dto -> sourceUrlFilter.add(dto.sourceUrl()))
//        .toList();
//  }

}
