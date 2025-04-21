package com.sprint.monew.domain.interest;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInterestRepository extends JpaRepository<UserInterest, UserInterestId> {

  int countDistinctByInterestId(UUID interestId);

  boolean existsByUserIdAndInterestId(UUID userId, UUID interestId);
}
