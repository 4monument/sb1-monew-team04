package com.sprint.monew.common.batch.util;

import com.sprint.monew.domain.article.api.ArticleApiDto;
import java.util.Collections;
import java.util.List;

// 스레드 4곳에서 공유되는 객체인데 변하면 위험하니 불변 객체로
public class Keywords {

  private final List<String> keywords;

  public Keywords(List<String> keywords) {
    this.keywords = Collections.unmodifiableList(keywords);
  }

  // O(N2)인데... 나중에 Hash 구조로?
  public List<ArticleApiDto> filter(List<ArticleApiDto> articleApiDtos) {
    return articleApiDtos.stream()
        .filter(this::isContainsIn)
        .toList();
  }

  public boolean isContainsIn(ArticleApiDto articleApiDto) {
    String summary = articleApiDto.summary();
    //summary.toLowerCase();
    return keywords.stream()
        .anyMatch(summary::contains);
  }
}
