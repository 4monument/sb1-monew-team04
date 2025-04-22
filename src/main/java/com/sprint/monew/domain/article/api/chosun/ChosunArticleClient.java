package com.sprint.monew.domain.article.api.chosun;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class ChosunArticleClient {

  private static final String baseUrl = "https://www.chosun.com/arc/outboundfeeds/rss";
  private static final String outputType = "/?outputType=xml";

  public ChosunArticleResponse getArticle(@PathVariable Category category) {
    RestClient restClient = RestClient.create();

    if (category == Category.ALL) {
      return restClient.get()
          .uri(baseUrl + outputType)
          .retrieve()
          .body(ChosunArticleResponse.class);
    }

    return restClient.get()
        .uri(baseUrl + "/category/" + category.getOriginalName() + outputType)
        .retrieve()
        .body(ChosunArticleResponse.class);
  }

  @Getter
  public enum Category {
    ALL(""),
    POLITICS("politics"),
    ECONOMY("economy"),
    NATIONAL("national"),
    INTERNATIONAL("international"),
    CULTURE_LIFE("culture-life"),
    OPINION("opinion"),
    SPORTS("sports"),
    ENTERTAINMENTS("entertainments");

    private final String originalName;

    Category(String originalName) {
      this.originalName = originalName;
    }
  }

}
