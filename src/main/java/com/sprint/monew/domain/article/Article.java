package com.sprint.monew.domain.article;

import com.sprint.monew.domain.article.articleinterest.ArticleInterest;
import com.sprint.monew.domain.article.articleview.ArticleView;
import com.sprint.monew.domain.interest.Interest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "articles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Article {

  @Id
  @GeneratedValue
  private UUID id;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Source source;

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

  @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
  List<ArticleInterest> articleInterests = new ArrayList<>();

  @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
  List<ArticleView> articleViews = new ArrayList<>();

  @Builder(access = AccessLevel.PRIVATE)
  private Article(Source source, String sourceUrl, String title, Instant publishDate,
      String summary) {
    this.source = source;
    this.sourceUrl = sourceUrl;
    this.title = title;
    this.publishDate = publishDate;
    this.summary = summary;
    this.deleted = false;
    this.createdAt = Instant.now();
  }

  private Article(UUID id, Source source, String sourceUrl, String title, Instant publishDate,
      String summary) {
    this.id = id;
    this.source = source;
    this.sourceUrl = sourceUrl;
    this.title = title;
    this.publishDate = publishDate;
    this.summary = summary;
    this.deleted = false;
    this.createdAt = Instant.now();
  }

  public static Article create(Source source, String sourceUrl, String title, Instant publishDate,
      String summary) {
    return Article.builder()
        .source(source)
        .sourceUrl(sourceUrl)
        .title(title)
        .publishDate(publishDate)
        .summary(summary)
        .build();
  }

  // JDBC 용
  public static Article createWithId(Source source, String sourceUrl, String title, Instant publishDate,
      String summary) {
    return new Article(UUID.randomUUID(), source, sourceUrl, title, publishDate, summary);
  }

  public enum Source {
    NAVER, HANKYUNG, CHOSUN, YONHAP
  }

  public void addInterest(Interest interest) {
    boolean exists = articleInterests.stream()
        .anyMatch(ai -> ai.getInterest().getId().equals(interest.getId()));
    if (exists) {
      return;
    }
    this.articleInterests.add(ArticleInterest.create(this, interest));
  }

  public void addView(ArticleView articleView) {
    this.articleViews.add(articleView);
  }

  public void logicallyDelete() {
    this.deleted = true;
  }
}
