package com.sprint.monew.domain.article.api.naver;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.OffsetDateTime;
import java.util.List;

public record NaverArticleResponse(
    @JsonFormat(pattern = "EEE, dd MMM yyyy HH:mm:ss Z", locale = "en", timezone = "Asia/Seoul")
    OffsetDateTime lastBuildDate,
    int total,
    int start,
    int display,
    List<ArticleItem> items
) {
  public record ArticleItem(
      String title,
      String originallink,
      String link,
      String description,
      @JsonFormat(pattern = "EEE, dd MMM yyyy HH:mm:ss Z", locale = "en", timezone = "Asia/Seoul")
      OffsetDateTime pubDate
  ) {}
}
