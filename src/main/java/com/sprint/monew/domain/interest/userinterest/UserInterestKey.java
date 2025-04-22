package com.sprint.monew.domain.interest.userinterest;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@Builder
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class UserInterestKey implements Serializable {
  private UUID userId;
  private UUID interestId;
}
