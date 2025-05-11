package com.sprint.monew.common.batch.support;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomExecutionContextKeys {
  NAVER_ARTICLE_DTOS("naverArticleDtos"),
  CHOSUN_ARTICLE_DTOS("chosunArticleDtos"),
  HANKYUNG_ARTICLE_DTOS("hankyungArticleDtos"),
  ARTICLE_IDS("articleIds");

  private final String key;
}
