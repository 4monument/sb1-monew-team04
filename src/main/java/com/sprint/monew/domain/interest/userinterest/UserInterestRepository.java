package com.sprint.monew.domain.interest.userinterest;

import com.sprint.monew.domain.notification.dto.UnreadInterestArticleCount;
import com.sprint.monew.domain.user.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserInterestRepository extends JpaRepository<UserInterest, UserInterestKey> {

  int countDistinctByInterestId(UUID interestId);

  boolean existsByUserIdAndInterestId(UUID userId, UUID interestId);

  List<UserInterest> findByUser(User user);

  @Query(
      "SELECT ui.interest.id as interestId, ui.interest.name as interestName, COUNT(DISTINCT ai.article.id) as unreadCount "
          + "FROM UserInterest ui "
          // 기사 관심사-관심사-사용자 를 관심사 Id로 조인 (특정 사용자의 관심사에 속하는 모든 기사)
          + "JOIN ArticleInterest ai ON ui.interest.id = ai.interest.id "
          // article_view 가 없는 article_interest 도 있어야함 (아직 읽지 않은 기사)
          // 따라서 article_interest 조인 후, article_view 를 left 조인
          + "LEFT JOIN ArticleView av ON av.article.id = ai.article.id AND av.user.id = ui.user.id "
          // 해당 유저가 아직 읽지 않은(av가 null 인) 경우만
          + "WHERE ui.user.id = :userId AND av IS NULL "
          // 관심사 별로 안 읽은 기사 count 하기 위해서 Group by 사용
          + "GROUP BY ui.interest.id, ui.interest.name")
  List<UnreadInterestArticleCount> countUnreadArticlesByInterest(@Param("userId") UUID userId);

}
