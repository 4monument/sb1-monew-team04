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

  public ArticleApiDto filter(ArticleApiDto articleApiDto) {
    if (isContainKeywords(articleApiDto.summary()) && isNewUrl(articleApiDto.sourceUrl())
        && !isPhoto(articleApiDto.sourceUrl())) {
      return articleApiDto;
    }
    return null;
  }

  private boolean isPhoto(String sourceUrl) {
    return sourceUrl.contains("photos");
  }

  private boolean isNewUrl(String sourceUrl) {
    return sourceUrlFilterSet.add(sourceUrl);
  }

  private boolean isContainKeywords(String summary) {
    String lowerCaseSummary = toLowerCaseAndTrim(summary);
    return keywords.stream()
        .anyMatch(lowerCaseSummary::contains);
  }

  public ArticleWithInterestList toArticleWithRelevantInterests(ArticleApiDto articleApiDto) {
    String lowerCaseSummary = toLowerCaseAndTrim(articleApiDto.summary());
    List<Interest> interestList = interests.stream()
        .filter(interest ->
            interest.getKeywords().stream()
                .anyMatch(keyword ->
                    lowerCaseSummary.contains(keyword.toLowerCase().trim()))
        )
        .toList();

    if (interestList.isEmpty()) {
      return null;
    }

    ArticleApiDto escapedArticleApiDto = ArticleApiDto.toEscapedArticleApiDto(articleApiDto);

    return new ArticleWithInterestList(
        escapedArticleApiDto.source(),
        escapedArticleApiDto.sourceUrl(),
        escapedArticleApiDto.title(),
        escapedArticleApiDto.publishDate(),
        escapedArticleApiDto.summary(),
        interestList
    );
  }

  public void clearBean() {
    this.interests.clear();
    this.keywords.clear();
    this.sourceUrlFilterSet.clear();
  }

  private String toLowerCaseAndTrim(String str) {
    if (str == null) {
      return null;
    }
    return str.toLowerCase().trim();
  }
}
