package com.sprint.monew.domain.notification;


import com.sprint.monew.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Table(name = "notifications")
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private UUID resourceId;

  // INTEREST, COMMENT
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ResourceType resourceType;

  @Column(nullable = false)
  private String content;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  private Instant updatedAt;

  @Column(nullable = false)
  private boolean confirmed;

  public Notification(User user, UUID resourceId, ResourceType resourceType, String content) {
    this.user = user;
    this.resourceId = resourceId;
    this.resourceType = resourceType;
    this.content = content;
    this.confirmed = false;
    this.createdAt = Instant.now();
    this.updatedAt = this.createdAt;
  }

  public void confirm() {
    this.confirmed = true;
  }
}
