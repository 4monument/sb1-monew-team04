package com.sprint.monew.domain.interest;

import static com.sprint.monew.domain.interest.util.SimilarityCalculator.calculateSimilarity;

import com.sprint.monew.common.util.CursorPageResponseDto;
import com.sprint.monew.domain.interest.dto.InterestCreateRequest;
import com.sprint.monew.domain.interest.dto.InterestDto;
import com.sprint.monew.domain.interest.dto.InterestSearchRequest;
import com.sprint.monew.domain.interest.dto.InterestSubscriptionInfoDto;
import com.sprint.monew.domain.interest.dto.InterestUpdateRequest;
import com.sprint.monew.domain.interest.exception.EmptyKeywordsException;
import com.sprint.monew.domain.interest.exception.InterestAlreadyExistsException;
import com.sprint.monew.domain.interest.exception.InterestNotFoundException;
import com.sprint.monew.domain.interest.exception.SubscriptionNotFound;
import com.sprint.monew.domain.interest.repository.InterestRepository;
import com.sprint.monew.domain.interest.subscription.Subscription;
import com.sprint.monew.domain.interest.subscription.SubscriptionDto;
import com.sprint.monew.domain.interest.subscription.SubscriptionRepository;
import com.sprint.monew.domain.user.User;
import com.sprint.monew.domain.user.UserRepository;
import com.sprint.monew.domain.user.exception.UserNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterestService {

  private final InterestRepository interestRepository;
  private final UserRepository userRepository;
  private final SubscriptionRepository subscriptionRepository;

  //관심사 목록 조회 - queryDsl 사용
  @Transactional(readOnly = true)
  public CursorPageResponseDto<InterestDto> getInterestsWithSubscriberInfo(
      InterestSearchRequest request, UUID requestUserId) {

    log.info("관심사 목록 조회 시작. 요청자 ID = {}", requestUserId);
    PageRequest pageRequest = PageRequest.of(0, request.limit() + 1);

    List<InterestSubscriptionInfoDto> result = interestRepository.getByNameOrKeywordsContaining(
        request.keyword(), request.cursor(),
        request.after(), request.direction(), request.orderBy(), pageRequest);

    log.debug("검색 및 정렬 조건 반영한 조회 결과 수= {}", result.size());

    boolean hasNext = result.size() > request.limit();
    if (hasNext) {
      result = result.subList(0, request.limit());
    }

    UUID nextCursor = null;
    Instant nextAfter = null;

    if (!result.isEmpty()) {
      nextCursor = result.get(result.size() - 1).getInterest().getId();
      nextAfter = interestRepository.findById(nextCursor)
          .map(Interest::getCreatedAt)
          .orElse(null);
    }

    int size = result.size();
    long totalElements = interestRepository.countByKeyword(request.keyword());

    List<InterestDto> interestDtos = result.stream()
        .map(i -> InterestDto.from(i,
            subscriptionRepository.existsByUserIdAndInterestId(requestUserId,
                i.getInterest().getId())))
        .toList();

    log.info("관심사 목록 조회 완료.");

    return new CursorPageResponseDto<>(
        interestDtos,
        nextCursor,
        nextAfter,
        size,
        totalElements,
        hasNext
    );
  }

  //관심사 등록
  @Transactional
  public InterestDto createInterest(InterestCreateRequest request) {

    log.info("관심사 등록 시작. 관심사 이름 = {}, 키워드 = {} ", request.name(), request.keywords());

    boolean existsSimilarName = interestRepository.existsByName(request.name());

    if (existsSimilarName) {
      throw InterestAlreadyExistsException.alreadyExistsException();
    }

    Interest interest = new Interest(request.name(), request.keywords());

    List<Interest> allInterests = interestRepository.findAll();

    for (Interest i : allInterests) {
      if (calculateSimilarity(i.getName(), interest.getName()) >= 0.8) {
        existsSimilarName = true;
        break;
      }
    }
    if (existsSimilarName) {
      throw InterestAlreadyExistsException.alreadyExistsException();
    }

    Interest savedInterest = interestRepository.save(interest);

    log.info("관심사 등록 완료. 관심사 ID = {}, 생성 시각 = {} ", savedInterest.getId(),
        savedInterest.getCreatedAt());

    return InterestDto.from(savedInterest, 0, false);
  }

  //관심사 구독
  @Transactional
  public SubscriptionDto subscribeToInterest(UUID interestId, UUID userId) {
    log.info("관심사 구독 시작. 요청자 ID = {} 관심사 ID = {}", userId, interestId);

    Interest interest = interestRepository.findById(interestId)
        .orElseThrow(() -> InterestNotFoundException.withId(interestId));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    Subscription subscribe = new Subscription(user, interest);
    subscriptionRepository.save(subscribe);

    log.info("관심사 구독 완료. 구독 ID = {}, 생성 시각 = {} ", subscribe.getId(), subscribe.getCreatedAt());

    return SubscriptionDto.from(subscribe,
        subscriptionRepository.countDistinctByInterestId(interestId));
  }


  //관심사 구독 취소
  @Transactional
  public boolean unsubscribeFromInterest(UUID interestId, UUID userId) {

    log.info("관심사 구독 취소 시작. 요청자 ID = {} 관심사 ID = {}", userId, interestId);

    Interest interest = interestRepository.findById(interestId)
        .orElseThrow(() -> InterestNotFoundException.withId(interestId));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    Subscription subscribe = subscriptionRepository.findByUserAndInterest(user, interest)
        .orElseThrow(SubscriptionNotFound::notFound);

    log.debug("찾은 구독 ID = {}", subscribe.getId());

    subscriptionRepository.delete(subscribe);

    log.info("관심사 구독 취소 완료.");

    return true;
  }

  //관심사 물리 삭제
  @Transactional
  public boolean deleteInterest(UUID interestId) {

    log.info("관심사 물리 삭제. 관심사 ID = {}", interestId);

    Interest interest = interestRepository.findById(interestId)
        .orElseThrow(() -> InterestNotFoundException.withId(interestId));

    interestRepository.delete(interest);

    log.info("관심사 물리 삭제 완료");
    return true;
  }

  //관심사 정보 수정
  @Transactional
  public InterestDto updateInterest(UUID requestUserId, UUID interestId,
      InterestUpdateRequest request) {

    log.info("관심사 정보 수정 시작. 요청자 ID = {} 관심사 ID = {}", requestUserId, interestId);

    if (request.keywords() == null || request.keywords().isEmpty()) {
      throw EmptyKeywordsException.emptyKeywords();
    }
    User user = null;
    if (requestUserId != null) {
      user = userRepository.findById(requestUserId)
          .orElseThrow(() -> UserNotFoundException.withId(requestUserId));
    }

    Interest interest = interestRepository.findById(interestId)
        .orElseThrow(() -> InterestNotFoundException.withId(interestId));

    log.debug("조회된 관심사 ID = {}", interest.getId());

    interest.updateKeywords(request.keywords());

    interestRepository.save(interest);

    log.info("관심사 정보 수정 완료. 수정 완료 시각 = {}", Instant.now());

    return InterestDto.from(
        interest,
        subscriptionRepository.countDistinctByInterestId(interestId),
        user == null ? true
            : subscriptionRepository.existsByUserIdAndInterestId(user.getId(), interestId)
    );
  }
}
