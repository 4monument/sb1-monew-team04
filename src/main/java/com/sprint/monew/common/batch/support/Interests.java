package com.sprint.monew.common.batch.support;


import com.sprint.monew.domain.article.api.ArticleApiDto;
import com.sprint.monew.domain.interest.Interest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Interests implements Serializable {

  private static final long serialVersionUID = 1L;
  private final List<Interest> interests;
  private final Set<String> keywords = ConcurrentHashMap.newKeySet();
  private final Set<String> sourceUrlFilterSet = ConcurrentHashMap.newKeySet();

  public Interests(List<Interest> interests) {
    this.interests = Collections.unmodifiableList(interests);
    interests.stream()
        .map(Interest::getKeywords)
        .flatMap(List::stream)
        .forEach(keywords::add);
  }

  public boolean isDuplicateUrl(ArticleApiDto articleApiDto) {
    return sourceUrlFilterSet.add(articleApiDto.sourceUrl());
  }

  public Boolean isContainKeywords(ArticleApiDto articleApiDto) {
    String summary = articleApiDto.summary();
    return keywords.stream()
        .anyMatch(summary::contains);
  }

  public ArticleWithInterestList toArticleWithRelevantInterests(ArticleApiDto articleApiDto) {
    String summary = articleApiDto.summary();
    List<Interest> interestList = interests.stream()
        .filter(interest ->
            interest.getKeywords().stream()
                .anyMatch(summary::contains))
        .toList();

    // 관련된 키워드가 없는 Aritcle은 필터링
    if (interestList.isEmpty()) {
      return null;
    }

    return new ArticleWithInterestList(
        articleApiDto.source(),
        articleApiDto.sourceUrl(),
        articleApiDto.title(),
        articleApiDto.publishDate(),
        articleApiDto.summary(),
        interestList
    );
  }

//  public Boolean isContainKeywords(ArticleApiDto articleApiDto) {
//    String summary = articleApiDto.summary();
//    return keywords.stream()
//        .anyMatch(summary::contains);
//  }
//  public Map<Article, List<Interest>> mapToArticleInterestsMap(List<Article> articles) {
//    return articles.stream()
//        .collect(Collectors.toMap(
//            article -> article,
//            article -> interests.stream()
//                .filter(
//                    interest -> interest.isContainsKeyword(article)) // 해당 뉴스에 해당하지 않는 Keyword 제외시키기
//                .toList()
//        ));
//  }
//
//  public Boolean validateKeywordContainingAndUniqueUrl(ArticleApiDto articleApiDto) {
//    return isContainKeywords(articleApiDto) && isDuplicateUrl(articleApiDto);
//  }
}
