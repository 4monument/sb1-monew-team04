package com.sprint.monew.domain.interest;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRepository extends JpaRepository<Interest, UUID> {

  // 특정 유저가 구독하고 있는 관심사 대상, 각 관심사별 구독자 수.

  // 관심사 이름 | 키워드 검색(부분일치)

  // 관심사 이름, 구독자수 페이지네이션 (오름차순, 내림차순) 조회

}
