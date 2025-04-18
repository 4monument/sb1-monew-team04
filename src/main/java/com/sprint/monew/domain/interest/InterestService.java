package com.sprint.monew.domain.interest;

import com.sprint.monew.domain.user.User;
import com.sprint.monew.domain.user.UserRepository;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterestService {

  private final InterestRepository interestRepository;
  private final UserRepository userRepository;

  //관심사 목록 조회
  public CursorPageResponseInterestDto getInterests(String keyword, String orderBy,
      String direction, String cursor, String after, int limit, String requestUserid) {
    /*
    - 검색어로 다음의 속성 중 하나라도 부분일치하는 데이터를 검색할 수 있습니다.
    - 관심사 이름, 키워드
    - 다음의 속성으로 정렬 및 커서 페이지네이션을 구현합니다.
    - 관심사 이름, 구독자 수
     */

    return null;
  }

  //관심사 등록
  public InterestDto createInterest(InterestDto interestDto) {
    /*
    - 80% 이상 유사한 이름의 관심사가 있다면 등록할 수 없음
    - 키워드는 여러 개를 가질 수 있으며, 뉴스 기사 검색에 활용됨
     */

    Interest interest = new Interest();
    interestRepository.save(interest);
    return null;
  }

  //관심사 구독
  public SubscriptionDto subscribeToInterest(String interestId, String userId) {
    Interest interest = interestRepository.findById(UUID.fromString(interestId))
        .orElseThrow(() -> new IllegalArgumentException("Interest not found"));
    User user = userRepository.findById(UUID.fromString(userId))
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    /*
    - 사용자는 관심사를 구독할 수 있습니다.
    - 구독한 관심사와 관련된 뉴스 기사가 등록되면 알림을 받을 수 있습니다.
     */

    return null;
  }


  //관심사 구독 취소
  public SubscriptionDto unsubscribeFromInterest(String interestId, String userId) {
    Interest interest = interestRepository.findById(UUID.fromString(interestId))
        .orElseThrow(() -> new IllegalArgumentException("Interest not found"));
    User user = userRepository.findById(UUID.fromString(userId))
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    /*

     */
    return null;
  }

  //관심사 물리 삭제
  public boolean deleteInterest(String interestId) {
    /*
    관심사는 삭제할 수 있습니다.
     */
    Interest interest = interestRepository.findById(UUID.fromString(interestId))
        .orElseThrow(() -> new IllegalArgumentException("Interest not found"));

    interestRepository.delete(interest);

    return false;
  }

  //관심사 정보 수정
  public boolean updateInterest(String interestId, InterestUpdateRequest interestUpdateRequest) {
    /*
    키워드만 수정할 수 있습니다.
     */
    return false;
  }
}
