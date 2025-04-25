package com.sprint.monew.common.batch.util;

import lombok.Getter;

@Getter
public enum CustomExecutionContextKeys {
  KEYWORDS("keywords"),
  INTERESTS("interests"),
  NAVER_ARTICLE_DTOS("naverArticleDtos"),
  CHOSUN_ARTICLE_DTOS("choshunArticleDtos"),
  HANKYUNG_ARTICLE_DTOS("hanyungArticleDtos");

  private final String key;

  CustomExecutionContextKeys(String key) {
    this.key = key;
  }
}
