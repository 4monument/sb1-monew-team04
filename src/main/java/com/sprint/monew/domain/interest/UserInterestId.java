package com.sprint.monew.domain.interest;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInterestId implements Serializable {
  private UUID userId;
  private UUID interestId;
}
