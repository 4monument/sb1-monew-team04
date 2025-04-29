package com.sprint.monew.domain.notification;


import com.sprint.monew.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Setter
@Table(name = "notifications")
@Entity
@NoArgsConstructor
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

  @CreationTimestamp
  @Column(nullable = false)
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
    this.createdAt = Instant.now();
    this.updatedAt = createdAt;
    this.confirmed = false;
  }

  public void confirm(Instant updatedAt) {
    this.confirmed = true;
    this.updatedAt = updatedAt;
  }
}
