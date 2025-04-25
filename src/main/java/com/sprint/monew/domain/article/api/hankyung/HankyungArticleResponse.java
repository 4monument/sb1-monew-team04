package com.sprint.monew.domain.article.api.hankyung;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.time.OffsetDateTime;
import java.util.List;

@JacksonXmlRootElement(localName = "rss")
public record HankyungArticleResponse(
    String version,
    Channel channel
) {

  public record Channel(
      String title,
      String link,
      String language,
      String copyright,

      @JsonFormat(pattern = "EEE, dd MMM yyyy HH:mm:ss Z", locale = "en", timezone = "Asia/Seoul")
      OffsetDateTime lastBuildDate,

      String description,

      @JacksonXmlElementWrapper(useWrapping = false)
      @JacksonXmlProperty(localName = "item")
      List<Item> items
  ) {

  }

  public record Item(
      String title,
      String link,
      String author,

      @JsonFormat(pattern = "EEE, dd MMM yyyy HH:mm:ss Z", locale = "en", timezone = "Asia/Seoul")
      OffsetDateTime pubDate
  ) {

  }
}
