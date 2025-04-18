package com.sprint.monew.domain.article;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "articles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {

  @Id
  @GeneratedValue
  private UUID id;

  @Column(nullable = false)
  private String source;

  @Column(nullable = false, length = 2048, unique = true)
  private String sourceUrl;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private Instant publishDate;

  @Column(nullable = false)
  private String summary;

  @Column(nullable = false)
  @ColumnDefault("false")
  private boolean deleted;

  @Builder(access = AccessLevel.PRIVATE)
  private Article(String source, String sourceUrl, String title, Instant publishDate, String summary) {
    this.source = source;
    this.sourceUrl = sourceUrl;
    this.title = title;
    this.publishDate = publishDate;
    this.summary = summary;
    this.deleted = false;
  }

  public static Article create(String source, String sourceUrl, String title, Instant publishDate,
      String summary) {
    return Article.builder()
        .source(source)
        .sourceUrl(sourceUrl)
        .title(title)
        .publishDate(publishDate)
        .summary(summary)
        .build();
  }

  public void logicallyDelete() {
    this.deleted = true;
  }
}
