package com.sprint.monew.common.batch.support;

import com.sprint.monew.domain.article.api.ArticleApiDto;
import com.sprint.monew.domain.interest.Interest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InterestContainer {

  private List<Interest> interests = new ArrayList<>();
  private final Set<String> keywords = ConcurrentHashMap.newKeySet();
  private final Set<String> sourceUrlFilterSet = ConcurrentHashMap.newKeySet();

  public void register(List<Interest> interests, List<String> sourceUrls) {
    clearBean();

    this.interests = interests;
    this.interests.stream()
        .map(Interest::getKeywords)
        .flatMap(List::stream)
        .map(String::toLowerCase)
        .forEach(keywords::add);
    this.sourceUrlFilterSet.addAll(sourceUrls);
  }


  public ArticleApiDto filter(ArticleApiDto articleApiDto){
    if (isContainKeywords(articleApiDto) && isNewUrl(articleApiDto)) {
      return articleApiDto;
    }
    return null;
  }

//  public Optional<ArticleApiDto> filter(ArticleApiDto articleApiDto){
//    if (isContainKeywords(articleApiDto) && !isDuplicateUrl(articleApiDto)) {
//      return Optional.of(articleApiDto);
//    }
//    return Optional.empty();
//  }
  public boolean isNewUrl(ArticleApiDto articleApiDto) {
    return sourceUrlFilterSet.add(articleApiDto.sourceUrl());
  }

  private boolean isContainKeywords(ArticleApiDto articleApiDto) {
    String lowerCaseSummary = articleApiDto.summary().toLowerCase();
    return keywords.stream()
        .anyMatch(lowerCaseSummary::contains);
  }

  public ArticleWithInterestList toArticleWithRelevantInterests(ArticleApiDto articleApiDto) {
    String summary = articleApiDto.summary();
    log.info("Summary: {}", summary);
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
  private void clearBean() {
    this.interests.clear();
    this.keywords.clear();
    this.sourceUrlFilterSet.clear();
  }
}
