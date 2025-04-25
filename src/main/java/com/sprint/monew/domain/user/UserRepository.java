package com.sprint.monew.domain.user;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

  boolean existsByEmail(String email);

  Optional<User> findByEmailAndDeletedFalse(String email);

  Optional<User> findByIdAndDeletedFalse(UUID userId);
}
