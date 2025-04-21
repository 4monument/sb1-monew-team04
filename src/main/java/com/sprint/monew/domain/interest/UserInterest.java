package com.sprint.monew.domain.interest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users_interests")
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UserInterestId.class)
public class UserInterest {

  @Id
  @Column(name = "user_id")
  private UUID userId;

  @Id
  @Column(name = "intrested_id")
  private UUID interestId;

  @Column(nullable = false)
  private Instant createdAt;
}
