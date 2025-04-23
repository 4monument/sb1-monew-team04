package com.sprint.monew.domain.interest.subscription;

import com.sprint.monew.domain.interest.Interest;
import com.sprint.monew.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users_interests", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "interest_id"})
})
@NoArgsConstructor
public class Subscription {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "interest_id")
  private Interest interest;

  @Column(nullable = false)
  private Instant createdAt;

  public Subscription(User user, Interest interest) {
    this.user = user;
    this.interest = interest;
    this.createdAt = Instant.now();
  }
}
