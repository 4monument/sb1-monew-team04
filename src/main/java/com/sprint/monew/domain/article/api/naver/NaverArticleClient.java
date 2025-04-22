package com.sprint.monew.domain.article.api.naver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class NaverArticleClient {

  private static final String url = "https://openapi.naver.com/v1/search/news.json";

  @Value("${article.api.naver.id:test}")
  private String id;

  @Value("${article.api.naver.secret:secret}")
  private String secret;

  public NaverArticleResponse getArticle(String query, int display, int start, String sort) {
    RestClient restClient = RestClient.builder()
        .defaultHeader("X-Naver-Client-Id", id)
        .defaultHeader("X-Naver-Client-Secret", secret)
        .build();

    String apiURL = url + "?query=" + query + "&display=" + display + "&start=" + start + "&sort=" + sort;

    return restClient.get()
        .uri(apiURL)
        .retrieve()
        .body(NaverArticleResponse.class);
  }
}
