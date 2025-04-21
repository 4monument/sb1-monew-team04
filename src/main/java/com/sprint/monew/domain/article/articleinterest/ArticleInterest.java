package com.sprint.monew.domain.article.articleinterest;

import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.interest.Interest;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
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
}
