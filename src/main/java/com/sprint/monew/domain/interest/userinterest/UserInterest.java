package com.sprint.monew.domain.interest.userinterest;

import com.sprint.monew.domain.interest.Interest;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users_interests")
@NoArgsConstructor
public class UserInterest {

  @EmbeddedId
  private UserInterestKey id;

  @MapsId("userId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @MapsId("interestId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "interest_id")
  private Interest interest;

  @Column(nullable = false)
  private Instant createdAt;

  public UserInterest(User user, Interest interest) {
    this.user = user;
    this.interest = interest;
    this.createdAt = Instant.now();
  }
}
