package com.sprint.monew.domain.comment;

import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.comment.like.Like;
import com.sprint.monew.domain.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "article_id", nullable = false)
  private Article article;

  @Column(nullable = false)
  private String content;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = false)
  @ColumnDefault("false")
  private boolean deleted;

  @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Like> likes = new ArrayList<>();

  @Builder(access = AccessLevel.PRIVATE)
  private Comment(User user, Article article, String content) {
    this.user = user;
    this.article = article;
    this.content = content;
    this.deleted = false;
  }

  public static Comment create(User user, Article article, String content) {
    return Comment.builder()
        .user(user)
        .article(article)
        .content(content)
        .build();
  }

  public void updateContent(String content) {
    if (this.deleted) {
      // 삭제된 메세지인 경우 예외 발생. 추후 커스텀 예외 추가 예정
    }
    this.content = content;
  }

  public void logicallyDelete() {
    this.deleted = true;
  }
}
