package com.sprint.monew.domain.article.api.chosun;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChosunArticleClient {

  private static final String baseUrl = "https://www.chosun.com/arc/outboundfeeds/rss";
  private static final String outputType = "/?outputType=xml";

  private final RestClient restClient;

  public ChosunArticleResponse getArticle(ChosunCategory category) {
    return restClient.get()
        .uri(baseUrl + "/category/" + category.getOriginalName() + outputType)
        .retrieve()
        .body(ChosunArticleResponse.class);
  }

  public ChosunArticleResponse getArticle() {
    return restClient.get()
        .uri(baseUrl + outputType)
        .retrieve()
        .body(ChosunArticleResponse.class);
  }

  @Getter
  public enum ChosunCategory {
    POLITICS("politics"),
    ECONOMY("economy"),
    NATIONAL("national"),
    INTERNATIONAL("international"),
    CULTURE_LIFE("culture-life"),
    OPINION("opinion"),
    SPORTS("sports"),
    ENTERTAINMENTS("entertainments");

    private final String originalName;

    ChosunCategory(String originalName) {
      this.originalName = originalName;
    }
  }

}
