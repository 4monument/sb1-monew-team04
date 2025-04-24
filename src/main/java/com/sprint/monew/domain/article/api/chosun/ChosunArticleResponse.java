package com.sprint.monew.domain.article.api.chosun;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.time.OffsetDateTime;
import java.util.List;

@JacksonXmlRootElement(localName = "rss")
public record ChosunArticleResponse(
    @JacksonXmlProperty(localName = "channel")
    ChosunChannel channel
) {

  public record ChosunChannel(
      @JacksonXmlProperty(localName = "title")
      @JacksonXmlCData
      String title,

      @JacksonXmlProperty(localName = "link")
      String link,

      @JacksonXmlProperty(localName = "description")
      @JacksonXmlCData
      String description,

      @JacksonXmlProperty(localName = "lastBuildDate")
      @JsonFormat(pattern = "EEE, dd MMM yyyy HH:mm:ss Z", locale = "en", timezone = "Asia/Seoul")
      OffsetDateTime lastBuildDate,

      @JacksonXmlProperty(localName = "language")
      String language,

      @JacksonXmlProperty(localName = "category")
      String category,

      @JacksonXmlProperty(localName = "ttl")
      int ttl,

      @JacksonXmlProperty(localName = "sy:updatePeriod")
      String updatePeriod,

      @JacksonXmlProperty(localName = "sy:updateFrequency")
      int updateFrequency,

      @JacksonXmlProperty(localName = "image")
      ChannelImage image,

      // item 목록 – useWrapping=false 로 <item>을 리스트로 처리
      @JacksonXmlElementWrapper(useWrapping = false)
      @JacksonXmlProperty(localName = "item")
      List<ChosunItem> items
  ) {

  }

  // <channel><image>…
  public record ChannelImage(
      @JacksonXmlProperty(localName = "url")
      String url,

      @JacksonXmlProperty(localName = "title")
      String title,

      @JacksonXmlProperty(localName = "link")
      String link
  ) {

  }

  // <item> 하나
  public record ChosunItem(
      @JacksonXmlProperty(localName = "title")
      @JacksonXmlCData
      String title,

      @JacksonXmlProperty(localName = "link")
      String link,

      @JacksonXmlProperty(localName = "guid")
      String guid,

      // namespace 처리 필요시 namespace 속성 추가 가능
      @JacksonXmlProperty(localName = "dc:creator")
      String creator,

      @JacksonXmlProperty(localName = "description")
      @JacksonXmlCData
      String description,

      @JacksonXmlProperty(localName = "pubDate")
      @JsonFormat(pattern = "EEE, dd MMM yyyy HH:mm:ss Z", locale = "en", timezone = "Asia/Seoul")
      OffsetDateTime pubDate,

      @JacksonXmlProperty(localName = "content:encoded")
      @JacksonXmlCData
      String contentEncoded,

      @JacksonXmlProperty(localName = "media:content")
      MediaContent mediaContent
  ) {

  }

  // <media:content …> 속성 매핑
  public record MediaContent(
      @JacksonXmlProperty(isAttribute = true, localName = "url")
      String url,

      @JacksonXmlProperty(isAttribute = true, localName = "type")
      String type,

      @JacksonXmlProperty(isAttribute = true, localName = "height")
      int height,

      @JacksonXmlProperty(isAttribute = true, localName = "width")
      int width
  ) {

  }
}
