package com.sprint.monew.common.batch.support;

import com.sprint.monew.domain.article.api.ArticleApiDto;
import com.sprint.monew.domain.interest.Interest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class InterestSingleton {

  private List<Interest> interests = new ArrayList<>();
  private final Set<String> keywords = ConcurrentHashMap.newKeySet();
  private final Set<String> sourceUrlFilterSet = ConcurrentHashMap.newKeySet();

  public void registerInterests(List<Interest> interests) {
    this.interests.clear();
    this.keywords.clear();
    this.sourceUrlFilterSet.clear();

    this.interests = Collections.unmodifiableList(interests);
    interests.stream()
        .map(Interest::getKeywords)
        .flatMap(List::stream)
        .forEach(keywords::add);
  }

  public ArticleApiDto filter(ArticleApiDto articleApiDto){
    if (isContainKeywords(articleApiDto) && !isDuplicateUrl(articleApiDto)) {
      return articleApiDto;
    }
    return null;
  }

  public void addSourceUrls(List<String> sourceUrls) {
    sourceUrlFilterSet.addAll(sourceUrls);
  }

  public boolean isDuplicateUrl(ArticleApiDto articleApiDto) {
    return sourceUrlFilterSet.add(articleApiDto.sourceUrl());
  }

  private boolean isContainKeywords(ArticleApiDto articleApiDto) {
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

}
