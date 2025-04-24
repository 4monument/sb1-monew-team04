package com.sprint.monew.domain.interest;

import com.sprint.monew.common.util.CursorPageResponseDto;
import com.sprint.monew.domain.interest.dto.InterestCreateRequest;
import com.sprint.monew.domain.interest.dto.InterestDto;
import com.sprint.monew.domain.interest.dto.InterestSearchRequest;
import com.sprint.monew.domain.interest.dto.InterestUpdateRequest;
import com.sprint.monew.domain.interest.subscription.Subscription;
import com.sprint.monew.domain.interest.subscription.SubscriptionDto;
import com.sprint.monew.domain.interest.subscription.SubscriptionRepository;
import com.sprint.monew.domain.user.User;
import com.sprint.monew.domain.user.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterestService {

  private final InterestRepository interestRepository;
  private final UserRepository userRepository;
  private final SubscriptionRepository subscriptionRepository;

  //관심사 목록 조회
  public CursorPageResponseDto<InterestDto> getInterests(InterestSearchRequest request,
      UUID requestUserid) {

    List<InterestDto> result = null;

    String orderBy = request.orderBy();

    if (orderBy.equalsIgnoreCase("name")) {
      result = getResultOrderByName(request, requestUserid);
    } else {
      result = getResultOrderBySubscriberCount(request, requestUserid);
    }

    boolean hasNext = result.size() > request.limit();
    if (hasNext) {
      result = result.subList(0, request.limit());
    }

    UUID nextCursor = null;
    Instant nextAfter = null;

    if (!result.isEmpty()) {
      nextCursor = result.get(result.size() - 1).id();
      nextAfter = interestRepository.findById(nextCursor)
          .map(Interest::getCreatedAt)
          .orElse(null);
    }

    int size = result.size();
    long totalElements = interestRepository.countByKeyword(request.keyword());

    return new CursorPageResponseDto<>(
        result,
        nextCursor,
        nextAfter,
        size,
        totalElements,
        hasNext
    );
  }

  private List<InterestDto> getResultOrderByName(InterestSearchRequest request,
      UUID requestUserid) {
    //todo - 리팩토링
    String orderBy = request.orderBy();
    UUID cursor = request.cursor();
    String keyword = request.keyword();
    Instant after = request.after();
    int limit = request.limit();
    String direction = request.direction();

    if (direction.equalsIgnoreCase("DESC")) {
      List<Interest> interests = interestRepository.findByNameOrKeywordsContainingOrderByNameDesc(
          keyword, cursor, after, limit);

      return interests.stream()
          .map(i -> InterestDto.from(i, subscriptionRepository.countDistinctByInterestId(i.getId()),
              subscriptionRepository.existsByUserIdAndInterestId(requestUserid, i.getId())))
          .toList();
    }
    List<Interest> interests = interestRepository.findByNameOrKeywordsContainingOrderByNameAsc(
        keyword, cursor, after, limit);

    return interests.stream()
        .map(i -> InterestDto.from(i, subscriptionRepository.countDistinctByInterestId(i.getId()),
            subscriptionRepository.existsByUserIdAndInterestId(requestUserid, i.getId())))
        .toList();
  }

  private List<InterestDto> getResultOrderBySubscriberCount(InterestSearchRequest request,
      UUID requestUserid) {

    String orderBy = request.orderBy();
    UUID cursor = request.cursor();
    String keyword = request.keyword();
    Instant after = request.after();
    int limit = request.limit();
    String direction = request.direction();

    if (direction.equalsIgnoreCase("DESC")) {
      List<InterestWithSubscriberCount> results = interestRepository
          .findByNameOrKeywordsContainingOrderBySubscriberCountDesc(keyword, cursor, after, limit);

      return results.stream().map(
              ic -> InterestDto.from(ic,
                  subscriptionRepository.existsByUserIdAndInterestId(requestUserid, ic.getId())))
          .toList();
    }

    List<InterestWithSubscriberCount> results = interestRepository
        .findByNameOrKeywordsContainingOrderBySubscriberCountAsc(keyword, cursor, after, limit);

    return results.stream().map(
            ic -> InterestDto.from(ic,
                subscriptionRepository.existsByUserIdAndInterestId(requestUserid, ic.getId())))
        .toList();
  }

  //관심사 등록
  public InterestDto createInterest(InterestCreateRequest request) {

    boolean existsSimilarName = interestRepository.existsByName(request.name());

    if (existsSimilarName) {
      throw new IllegalArgumentException("동일한 이름의 관심사가 이미 존재합니다.");
    }

    Interest interest = new Interest(request.name(), request.keywords());

    boolean existsSimilarName = interestRepository.existsByName(request.name());

    if (existsSimilarName) {
      throw new IllegalArgumentException("동일한 이름의 관심사가 이미 존재합니다.");
    }

    //findAll()해서 매번 다 비교하면 너무 오래걸리지 않을까?
    List<Interest> allInterests = interestRepository.findAll();

    for (Interest i : allInterests) {
      if (calculateSimilarity(i.getName(), interest.getName()) >= 0.8) {
        existsSimilarName = true;
        break;
      }
    }
    if (existsSimilarName) {
      throw new IllegalArgumentException("유사한 이름의 관심사가 이미 존재합니다.");
    }

    Interest savedInterest = interestRepository.save(interest);

    return InterestDto.from(savedInterest, 0, false);
  }

  //관심사 구독
  public SubscriptionDto subscribeToInterest(UUID interestId, UUID userId) {
    Interest interest = interestRepository.findById(interestId)
        .orElseThrow(() -> new IllegalArgumentException("Interest not found"));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    Subscription subscribe = new Subscription(user, interest);
    subscriptionRepository.save(subscribe);

    return SubscriptionDto.from(subscribe,
        subscriptionRepository.countDistinctByInterestId(interestId));
  }


  //관심사 구독 취소
  public boolean unsubscribeFromInterest(UUID interestId, UUID userId) {
    Interest interest = interestRepository.findById(interestId)
        .orElseThrow(() -> new IllegalArgumentException("Interest not found"));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    Subscription subscribe = subscriptionRepository.findByUserAndInterest(user, interest)
        .orElseThrow(
            () -> new IllegalArgumentException("UserInterest not found")
        );

    subscriptionRepository.delete(subscribe);

    return true;
  }

  //관심사 물리 삭제
  public boolean deleteInterest(UUID interestId) {

    Interest interest = interestRepository.findById(interestId)
        .orElseThrow(() -> new IllegalArgumentException("Interest not found"));

    interestRepository.delete(interest);

    return true;
  }

  //관심사 정보 수정
  public InterestDto updateInterest(UUID requestUserId, UUID interestId,
      InterestUpdateRequest request) {

    if (request.keywords() == null || request.keywords().isEmpty()) {
      throw new IllegalArgumentException("keywords is empty");
    }
    User user = null;
    if (requestUserId != null) {
      user = userRepository.findById(requestUserId)
          .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    Interest interest = interestRepository.findById(interestId)
        .orElseThrow(() -> new IllegalArgumentException("Interest not found"));

    interest.updateKeywords(request.keywords());

    interestRepository.save(interest);

    return InterestDto.from(
        interest,
        subscriptionRepository.countDistinctByInterestId(interestId),
        user == null ? true
            : subscriptionRepository.existsByUserIdAndInterestId(user.getId(), interestId)
    );
  }

  //두 문자열 간 유사도 검사
  private double calculateSimilarity(String str1, String str2) {
    if (str1.equals(str2)) {
      return 1.0;
    }

    if (str1 == null || str2 == null || str1.isEmpty() || str2.isEmpty()) {
      return 0.0;
    }

    int distance = levenshteinDistance(str1, str2);

    // 최대 가능한 거리 (더 긴 문자열의 길이)
    int maxLength = Math.max(str1.length(), str2.length());

    //유사도 계산
    return 1.0 - ((double) distance / maxLength);
  }

  /**
   * 레벤슈타인 거리를 계산 한 문자열에서 다른 문자열로 변환하는 데 필요한 최소 편집 연산(삽입, 삭제, 대체)의 수.
   */
  private int levenshteinDistance(String str1, String str2) {
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
