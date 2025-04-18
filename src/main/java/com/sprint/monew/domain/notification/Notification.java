package com.sprint.monew.domain.notification;


import com.sprint.monew.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Getter
@Setter
@Table(name = "notifications")
@Entity
@NoArgsConstructor
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @ManyToOne(optional = false)
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

  @Column(nullable = false)
  private Instant createdAt;

  @Column(nullable = true)
  //Not Null 이어야 하지 않나?
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
