package com.sprint.monew.domain.article.api.naver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverArticleClient {

  private static final String url = "https://openapi.naver.com/v1/search/news.json";

  private final RestClient restClient;

  @Value("${article.api.naver.id:test}")
  private String id;

  @Value("${article.api.naver.secret:secret}")
  private String secret;

  public NaverArticleResponse getArticle(String query, int display, int start, String sort) {
    String apiURL = url + "?query=" + query + "&display=" + display + "&start=" + start + "&sort=" + sort;

    return restClient.get()
        .uri(apiURL)
        .header("X-Naver-Client-Id", id)
        .header("X-Naver-Client-Secret", secret)
        .retrieve()
        .body(NaverArticleResponse.class);
  }
}
