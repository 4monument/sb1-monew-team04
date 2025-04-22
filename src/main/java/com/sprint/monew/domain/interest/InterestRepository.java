package com.sprint.monew.domain.interest;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InterestRepository extends JpaRepository<Interest, UUID> {

  boolean existsByName(String name);

  List<Interest> findByNameContaining(String name);

  //관심사 이름 or 키워드 검색(부분일치) & 관심사 이름 오름차순 페이지네이션
  @Query(value =
      "SELECT * FROM interests i WHERE " +
          "(:keyword IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
          "EXISTS (SELECT 1 FROM jsonb_array_elements_text(i.keywords) k " +
          "WHERE LOWER(k) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
          "AND (:cursorId IS NULL OR "
          + " (cast(:after as timestamptz) IS NOT NULL AND i.created_at > :after) OR"
          + " (cast(:after as timestamptz) IS NOT NULL AND i.created_at > :after AND i.id >: cursorId)) "
          + "ORDER BY i.name ASC, i.id ASC "
          + "LIMIT :limit",
      nativeQuery = true)
  List<Interest> findByNameOrKeywordsContainingOrderByNameAsc(String keyword, String cursorId,
      String after, int limit);

  //관심사 이름 or 키워드 검색(부분일치) & 관심사 이름 내림차순 페이지네이션
  @Query(value =
      "SELECT * FROM interests i WHERE " +
          "(:keyword IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
          "EXISTS (SELECT 1 FROM jsonb_array_elements_text(i.keywords) k " +
          "WHERE LOWER(k) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
          "AND (:cursorId IS NULL OR "
          + " (cast(:after as timestamptz) IS NOT NULL AND i.created_at < :after) OR"
          + " (cast(:after as timestamptz) IS NOT NULL AND i.created_at < :after AND i.id <: cursorId)) "
          + "ORDER BY i.name DESC, i.id DESC "
          + "LIMIT :limit",
      nativeQuery = true)
  List<Interest> findByNameOrKeywordsContainingOrderByNameDesc(String keyword, String cursorId,
      String after, int limit);

  //관심사 이름 or 키워드 검색(부분일치) & 구독자 수 오름차순 페이지네이션
  @Query(value =
      "SELECT i.*, COUNT(ui.user_id) AS subscriber_count FROM interests i " +
          "LEFT JOIN users_interests ui ON i.id = ui.interest_id " +
          "WHERE (:keyword IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
          "EXISTS (SELECT 1 FROM jsonb_array_elements_text(i.keywords) k " +
          "WHERE LOWER(k) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
          "AND (:cursor IS NULL OR i.id > :cursor) " +
          "AND (:after IS NULL OR i.created_at > :after) " +
          "GROUP BY i.id " +
          "ORDER BY COUNT(ui.user_id) ASC, i.id ASC " +
          "LIMIT :limit",
      nativeQuery = true)
  List<InterestWithSubscriberCount> findByNameOrKeywordsContainingOrderBySubscriberCountAsc(String keyword,
      String cursorId,
      String after, int limit);

  //관심사 이름 or 키워드 검색(부분일치) & 구독자 수 내림차순 페이지네이션
  @Query(value =
      "SELECT i.*, COUNT(ui.user_id) AS subscriber_count FROM interests i " +
          "LEFT JOIN users_interests ui ON i.id = ui.interest_id " +
          "WHERE (:keyword IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
          "EXISTS (SELECT 1 FROM jsonb_array_elements_text(i.keywords) k " +
          "WHERE LOWER(k) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
          "AND (:cursor IS NULL OR i.id < :cursor) " +
          "AND (:after IS NULL OR i.created_at < :after) " +
          "GROUP BY i.id " +
          "ORDER BY COUNT(ui.user_id) DESC, i.id DESC " +
          "LIMIT :limit",
      nativeQuery = true)
  List<InterestWithSubscriberCount> findByNameOrKeywordsContainingOrderBySubscriberCountDesc(String keyword,
      String cursorId,
      String after, int limit);


  @Query(value =
      "SELECT * FROM interests i WHERE " +
          "(:keyword IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
          "EXISTS (SELECT 1 FROM jsonb_array_elements_text(i.keywords) k " +
          "WHERE LOWER(k) LIKE LOWER(CONCAT('%', :keyword, '%')))) ",
      nativeQuery = true)
  int countByKeyword(String keyword);

}
