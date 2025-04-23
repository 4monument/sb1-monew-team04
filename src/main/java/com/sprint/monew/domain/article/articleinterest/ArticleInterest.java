package com.sprint.monew.domain.article.articleinterest;

import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.interest.Interest;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(name = "articles_interests", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"article_id", "interest_id"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleInterest {

  @Id
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "article_id")
  private Article article;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "interest_id")
  private Interest interest;

  @Builder(access = AccessLevel.PRIVATE)
  private ArticleInterest(Article article, Interest interest) {
    this.id = UUID.randomUUID();
    this.article = article;
    this.interest = interest;
  }

  public static ArticleInterest create(Article article, Interest interest) {
    return ArticleInterest.builder()
        .article(article)
        .interest(interest)
        .build();
  }
}
