package com.sprint.monew.common.batch.config;

import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.ARTICLE_IDS;
import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.CHOSUN_ARTICLE_DTOS;
import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.HANKYUNG_ARTICLE_DTOS;
import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.NAVER_ARTICLE_DTOS;

import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PromotionListenerConfig {

  @Bean
  public ExecutionContextPromotionListener naverPromotionListener() {
    ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
    listener.setKeys(new String[]{NAVER_ARTICLE_DTOS.getKey()});
    return listener;
  }

  @Bean
  public ExecutionContextPromotionListener chosunPromotionListener() {
    ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
    listener.setKeys(new String[]{CHOSUN_ARTICLE_DTOS.getKey()});
    return listener;
  }

  @Bean
  public ExecutionContextPromotionListener hankyungPromotionListener() {
    ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
    listener.setKeys(new String[]{HANKYUNG_ARTICLE_DTOS.getKey()});
    return listener;
  }

  @Bean
  public ExecutionContextPromotionListener restoreArticleIdsPromotionListener() {
    ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
    listener.setKeys(new String[]{ARTICLE_IDS.getKey()});
    return listener;
  }
}
