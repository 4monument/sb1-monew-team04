package com.sprint.monew.domain.article.articleview;

import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "articles_views", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "article_id"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleView {

  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "article_id", nullable = false)
  private Article article;

  @CreatedDate
  @Column(nullable = false)
  private Instant createdAt;

  @Builder(access = AccessLevel.PRIVATE)
  private ArticleView(User user, Article article) {
    this.user = user;
    this.article = article;
    this.createdAt = Instant.now();
  }

  public static ArticleView create(User user, Article article) {
    return ArticleView.builder()
        .user(user)
        .article(article)
        .build();
  }
}
