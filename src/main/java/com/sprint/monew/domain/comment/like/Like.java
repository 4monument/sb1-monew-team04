package com.sprint.monew.domain.comment.like;

import com.sprint.monew.domain.comment.Comment;
import com.sprint.monew.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Table(name = "likes")
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class Like {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "comment_id", nullable = false)
  private Comment comment;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  public Like(User user, Comment comment) {
      this.user = user;
      this.comment = comment;
      this.createdAt = Instant.now();
  }
}
