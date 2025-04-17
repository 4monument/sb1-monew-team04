package com.sprint.monew.domain.notification;


import com.sprint.monew.domain.user.User;
import jakarta.persistence.Entity;
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
  @JoinColumn(name = "user_id")
  private User user;

  private UUID resourceId;

  // INTEREST, COMMENT
  private ResourceType resourceType;

  private String content;

  private Instant createdAt;

  private Instant updatedAt;

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
}
