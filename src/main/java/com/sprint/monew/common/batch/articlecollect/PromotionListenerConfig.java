package com.sprint.monew.common.batch.articlecollect;

import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.CHOSUN_ARTICLE_DTOS;
import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.HANKYUNG_ARTICLE_DTOS;
import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.INTERESTS;
import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.NAVER_ARTICLE_DTOS;

import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PromotionListenerConfig {

  private static final String INTERESTS_KEY = INTERESTS.getKey();
  private static final String NAVER_ARTICLE_DTOS_KEY = NAVER_ARTICLE_DTOS.getKey();
  private static final String CHOSUN_ARTICLE_DTOS_KEY = CHOSUN_ARTICLE_DTOS.getKey();
  private static final String HANKYUNG_ARTICLE_DTOS_KEY = HANKYUNG_ARTICLE_DTOS.getKey();


  @Bean
  public ExecutionContextPromotionListener interestsFetchPromotionListener(){
    ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
    listener.setKeys(new String[] {INTERESTS_KEY});
    return listener;
  }

  @Bean
  public ExecutionContextPromotionListener naverPromotionListener() {
    //new RepositoryItemReader<>()
    ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
    listener.setKeys(new String[] {NAVER_ARTICLE_DTOS_KEY});
    return listener;
  }

  @Bean
  public ExecutionContextPromotionListener chosunPromotionListener() {
    ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
    listener.setKeys(new String[] { CHOSUN_ARTICLE_DTOS_KEY});
    return listener;
  }
}
