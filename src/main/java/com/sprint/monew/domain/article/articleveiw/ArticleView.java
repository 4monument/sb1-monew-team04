package com.sprint.monew.domain.article.articleveiw;

import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "articles_views")
@NoArgsConstructor
public class ArticleView {

  @EmbeddedId
  private ArticleViewKey id;

  @MapsId("articleId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "article_id")
  private Article article;

  @MapsId("userId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(nullable = false)
  private Instant createdAt;

  public ArticleView(ArticleViewKey id, Article article, User user) {
    this.id = id;
    this.article = article;
    this.user = user;
    this.createdAt = Instant.now();
  }
}
