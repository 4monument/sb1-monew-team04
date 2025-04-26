package com.sprint.monew.common.batch.util;


import com.sprint.monew.domain.article.api.ArticleApiDto;
import com.sprint.monew.domain.interest.Interest;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Interests implements Serializable {

  private static final long serialVersionUID = 1L;
  private final List<Interest> interests;
  private final Set<String> sourceUrlFilterSet = ConcurrentHashMap.newKeySet();

  public Interests(List<Interest> interests) {
    this.interests = Collections.unmodifiableList(interests);
  }

  public boolean isDuplicateUrl(ArticleApiDto articleApiDto) {
    return sourceUrlFilterSet.add(articleApiDto.sourceUrl());
  }

  public ArticleWithInterestList toArticleAndRelevantInterests(ArticleApiDto articleApiDto) {
    String summary = articleApiDto.summary();
    List<Interest> interestList = interests.stream()
        .filter(interest ->
            interest.getKeywords().stream()
            .anyMatch(summary::contains))
        .toList();

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
