package com.sprint.monew.domain.interest.userinterest;

import com.sprint.monew.domain.interest.Interest;
import com.sprint.monew.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users_interests", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "interest_id"})
})
@NoArgsConstructor
public class UserInterest {

  @Id
  private UUID id;

  @MapsId("userId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @MapsId("interestId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "interest_id", nullable = false)
  private Interest interest;

  @Column(nullable = false)
  private Instant createdAt;

  public UserInterest(User user, Interest interest) {
    this.id = UUID.randomUUID();
    this.user = user;
    this.interest = interest;
    this.createdAt = Instant.now();
  }
}
