package com.sprint.monew.common.batch.util;

import java.util.Collections;
import java.util.List;
import lombok.Getter;


// API 4곳에서 공유되는 객체인데 변하면 위험하니 불변 객체로
@Getter
public class Keywords {

  private final List<String> keywords;

  public Keywords(List<String> keywords) {
    this.keywords = Collections.unmodifiableList(keywords);
  }
}
