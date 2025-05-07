package com.sprint.monew.domain.interest.subscription;

import com.sprint.monew.domain.interest.Interest;
import com.sprint.monew.domain.notification.dto.UnreadInterestArticleCount;
import com.sprint.monew.domain.user.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

  //todo - 해당 메서드들 검증할 테스트코드 작성
  int countDistinctByInterestId(UUID interestId);

  boolean existsByUserIdAndInterestId(UUID userId, UUID interestId);

  Optional<Subscription> findByUserAndInterest(User user, Interest interest);
  
  //특정시간 이후 등록된 모든 관심사-기사, 관심사별로 기사수를 가져옴
  //관심사-유저와 위 테이블 조인
  //유저-관심사-기사수를 구한다. -> 해당 엔티티마다 notification 전부 만들어야한다.
  @Query(
      "SELECT s.interest AS interest, s.user AS user, COUNT(ai) AS articleCount " +
          "FROM Subscription as s " +
          "JOIN ArticleInterest ai ON s.interest = ai.interest " +
          "WHERE ai.createdAt > :afterAt " +
          "GROUP BY s.interest, s.user"
  )
  List<UnreadInterestArticleCount> findNewArticleCountWithUserInterest(
      @Param("afterAt") Instant afterAt);

}
