package com.sprint.monew.common.batch.articlecollect;

import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.CHOSUN_ARTICLE_DTOS;
import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.DB_SOURCEURS;
import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.HANKYUNG_ARTICLE_DTOS;
import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.INTERESTS;
import static com.sprint.monew.common.batch.support.CustomExecutionContextKeys.NAVER_ARTICLE_DTOS;

import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PromotionListenerConfig {

  @Bean
  public ExecutionContextPromotionListener interestsFetchPromotionListener(){
    ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
    listener.setKeys(new String[] {INTERESTS.getKey()});
    return listener;
  }

  @Bean
  public ExecutionContextPromotionListener naverPromotionListener() {
    //new RepositoryItemReader<>()
    ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
    listener.setKeys(new String[] {NAVER_ARTICLE_DTOS.getKey()});
    return listener;
  }

  @Bean
  public ExecutionContextPromotionListener chosunPromotionListener() {
    ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
    listener.setKeys(new String[] {CHOSUN_ARTICLE_DTOS.getKey()});
    return listener;
  }


  @Bean
  public ExecutionContextPromotionListener dbSourceUrlPromotionListener() {
    ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
    listener.setKeys(new String[] { DB_SOURCEURS.getKey() });
    return listener;
  }
}
