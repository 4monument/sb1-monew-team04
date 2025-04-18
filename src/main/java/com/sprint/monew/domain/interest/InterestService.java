package com.sprint.monew.domain.interest;

import com.sprint.monew.common.util.CursorPageResponseDto;
import com.sprint.monew.domain.user.User;
import com.sprint.monew.domain.user.UserRepository;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterestService {

  private final InterestRepository interestRepository;
  private final UserRepository userRepository;

  //관심사 목록 조회
  public CursorPageResponseDto getInterests(String keyword, String orderBy,
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
  public InterestDto createInterest(InterestCreateRequest request) {

    Interest interest = new Interest(request.name(), request.keywords());

    boolean existsSimilarName = interestRepository.existsByName(request.name());

    if( existsSimilarName ){
      throw new IllegalArgumentException("동일한 이름의 관심사가 이미 존재합니다.");
    }

    //findAll()해서 매번 다 비교하면 너무 오래걸리지 않을까?
    List<Interest> allInterests = interestRepository.findAll();

    for(Interest i : allInterests) {
      if(calculateSimilarity(i.getName(), interest.getName()) >= 0.8){
        existsSimilarName = true;
        break;
      }
    }
    if( existsSimilarName ){
      throw new IllegalArgumentException("유사한 이름의 관심사가 이미 존재합니다.");
    }

    Interest savedInterest = interestRepository.save(interest);

    return InterestDto.from(savedInterest, 0, false);
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

    Interest interest = interestRepository.findById(UUID.fromString(interestId))
        .orElseThrow(() -> new IllegalArgumentException("Interest not found"));

    interestRepository.delete(interest);

    return true;
  }

  //관심사 정보 수정
  public boolean updateInterest(String interestId, InterestUpdateRequest interestUpdateRequest) {
    /*
    키워드만 수정할 수 있습니다.
     */
    return false;
  }

  private double calculateSimilarity(String str1, String str2){
    if( str1.equals(str2)){
      return 1.0;
    }

    if(str1 == null || str2 == null || str1.isEmpty() || str2.isEmpty()){
      return 0.0;
    }

    int distance = levenshteinDistance(str1, str2);

    // 최대 가능한 거리 (더 긴 문자열의 길이)
    int maxLength = Math.max(str1.length(), str2.length());

    //유사도 계산
    return 1.0 - ((double) distance / maxLength);
  }

  /**
   * 레벤슈타인 거리를 계산
   * 한 문자열에서 다른 문자열로 변환하는 데 필요한 최소 편집 연산(삽입, 삭제, 대체)의 수.
   */
  private int levenshteinDistance(String str1, String str2){
    int[][] dp = new int[str1.length() + 1][str2.length() + 1];

    for (int i = 0; i <= str1.length(); i++) {
      dp[i][0] = i;
    }

    for (int j = 0; j <= str2.length(); j++) {
      dp[0][j] = j;
    }

    for (int i = 1; i <= str1.length(); i++) {
      for (int j = 1; j <= str2.length(); j++) {
        int cost = (str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1;
        dp[i][j] = Math.min(
            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
            dp[i - 1][j - 1] + cost
        );
      }
    }
    return dp[str1.length()][str2.length()];
  }

}
