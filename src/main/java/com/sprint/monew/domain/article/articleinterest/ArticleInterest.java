package com.sprint.monew.domain.article.articleinterest;

import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.interest.Interest;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "articles_interests")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleInterest {

  @EmbeddedId
  private ArticleInterestKey id;

  @MapsId("articleId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "article_id")
  private Article article;

  @MapsId("interestId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "interest_id")
  private Interest interest;

  @Column(nullable = false)
  private Instant createdAt;

  @Builder(access = AccessLevel.PRIVATE)
  private ArticleInterest(ArticleInterestKey id, Article article, Interest interest) {
    this.id = id;
    this.article = article;
    this.interest = interest;
  }

  public static ArticleInterest create(Article article, Interest interest) {
    ArticleInterestKey articleInterestKey = new ArticleInterestKey(article.getId(),
        interest.getId());
    return ArticleInterest.builder()
        .article(article)
        .interest(interest)
        .id(articleInterestKey)
        .build();
  }
}
