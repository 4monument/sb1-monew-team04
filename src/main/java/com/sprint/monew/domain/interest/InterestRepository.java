package com.sprint.monew.domain.interest;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InterestRepository extends JpaRepository<Interest, UUID>,
    CustomInterestRepository {

  boolean existsByName(String name);

  @Query(value =
      "SELECT COUNT(i) FROM interests i WHERE " +
          "(:keyword IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
          "EXISTS (SELECT 1 FROM jsonb_array_elements_text(i.keywords) k " +
          "WHERE LOWER(k) LIKE LOWER(CONCAT('%', :keyword, '%')))) ",
      nativeQuery = true)
  int countByKeyword(String keyword);

}
