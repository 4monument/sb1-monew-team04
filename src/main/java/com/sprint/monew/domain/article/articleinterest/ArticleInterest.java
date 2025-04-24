package com.sprint.monew.domain.article.articleinterest;

import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.interest.Interest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "articles_interests", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"article_id", "interest_id"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleInterest {

  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "article_id", nullable = false)
  private Article article;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "interest_id", nullable = false)
  private Interest interest;

  @Column(nullable = false)
  private Instant createdAt;

  @Builder(access = AccessLevel.PRIVATE)
  private ArticleInterest(Article article, Interest interest) {
    this.article = article;
    this.interest = interest;
    this.createdAt = Instant.now();
  }

  public static ArticleInterest create(Article article, Interest interest) {
    return ArticleInterest.builder()
        .article(article)
        .interest(interest)
        .build();
  }
}
