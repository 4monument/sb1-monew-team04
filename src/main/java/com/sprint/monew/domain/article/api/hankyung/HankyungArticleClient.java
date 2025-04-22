package com.sprint.monew.domain.article.api.hankyung;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class HankyungArticleClient {

  private static final String baseUrl = "https://www.hankyung.com/feed/";

  public HankyungArticleResponse getArticle(Category category) {
    RestClient restClient = RestClient.create();

    return restClient.get()
        .uri(baseUrl + category.getOriginalName())
        .retrieve()
        .body(HankyungArticleResponse.class);
  }

  @Getter
  public enum Category {
    ALL("all-news"),
    ECONOMY("economy"),
    IT("it"),
    INTERNATIONAL("international"),
    LIFE("life"),
    SPORTS("sports"),
    VIDEO("video"),
    FINANCE("finance"),
    REALESTATE("realEstate"),
    POLITICS("politics"),
    SOCIETY("society"),
    OPINION("opinion"),
    ENTERTAINMENT("entertainment");

    private final String originalName;

    Category(String originalName) {
      this.originalName = originalName;
    }
  }

}
