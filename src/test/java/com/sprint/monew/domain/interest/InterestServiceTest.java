package com.sprint.monew.domain.interest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.monew.common.util.CursorPageResponseDto;
import com.sprint.monew.domain.interest.dto.InterestCreateRequest;
import com.sprint.monew.domain.interest.dto.InterestDto;
import com.sprint.monew.domain.interest.dto.InterestSearchRequest;
import com.sprint.monew.domain.interest.dto.InterestSubscriptionInfoDto;
import com.sprint.monew.domain.interest.dto.InterestUpdateRequest;
import com.sprint.monew.domain.interest.exception.EmptyKeywordsException;
import com.sprint.monew.domain.interest.exception.InterestAlreadyExistsException;
import com.sprint.monew.domain.interest.exception.InterestNotFoundException;
import com.sprint.monew.domain.interest.repository.InterestRepository;
import com.sprint.monew.domain.interest.subscription.Subscription;
import com.sprint.monew.domain.interest.subscription.SubscriptionDto;
import com.sprint.monew.domain.interest.subscription.SubscriptionRepository;
import com.sprint.monew.domain.user.User;
import com.sprint.monew.domain.user.UserRepository;
import com.sprint.monew.domain.user.exception.UserNotFoundException;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("관심사 서비스 테스트")
class InterestServiceTest {

  @Mock
  private InterestRepository interestRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private SubscriptionRepository subscriptionRepository;

  @InjectMocks
  private InterestService interestService;

  // 테스트에 사용할 Interest 객체 리스트
  private List<Interest> interests;

  // 테스트 유저
  private UUID userId;
  private User user;

  @BeforeEach
  void setUp() {
    interests = new ArrayList<>();

    // 첫 번째 테스트 데이터 - 프로그래밍 관심사
    Interest programming = new Interest("프로그래밍", Arrays.asList("Java", "Spring", "Python", "개발"));
    setPrivateField(programming, "id", UUID.fromString("a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d"));
    interests.add(programming);

    // 두 번째 테스트 데이터 - 음악 관심사
    Interest music = new Interest("음악/예술", Arrays.asList("클래식", "재즈", "힙합"));
    setPrivateField(music, "id", UUID.fromString("b2c3d4e5-f6a7-5b6c-9d0e-1f2a3b4c5d6"));
    interests.add(music);

    // 세 번째 테스트 데이터 - 여행 관심사
    Interest travel = new Interest("여행", Arrays.asList("유럽", "아시아", "배낭여행", "맛집탐방"));
    setPrivateField(travel, "id", UUID.fromString("c3d4e5f6-a7b8-6c7d-0e1f-2a3b4c5d6e7"));
    interests.add(travel);

    // 네 번째 테스트 데이터 - 개발 관심사
    Interest develop = new Interest("IT 기술", Arrays.asList("개발", "백엔드", "프론트엔드", "AI", "개발자"));
    setPrivateField(develop, "id", UUID.fromString("d4e5f6a7-b86c-7d0e-1f2a-3b4c5d6e7f8"));
    interests.add(develop);

    // 테스트 유저
    userId = UUID.randomUUID();
    user = new User(
        userId,
        "test@example.com",
        "테스트유저",
        "hashedPassword123",
        Instant.now(),
        false
    );
  }

  // 리플렉션을 사용하여 private 필드 값 설정
  private void setPrivateField(Object target, String fieldName, Object value) {
    try {
      Field field = target.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(target, value);
    } catch (Exception e) {
      throw new RuntimeException("Failed to set private field: " + fieldName, e);
    }
  }


  @Nested
  @DisplayName("관심사 등록")
  class CreateInterest {

    @Test
    @DisplayName("성공")
    void createInterestSuccess() {
      //given
      InterestCreateRequest request = new InterestCreateRequest(
          "개발", List.of("개발자", "기술", "개발", "AI")
      );
      Interest mockInterest = new Interest(request.name(), request.keywords());

      when(interestRepository.findAll()).thenReturn(interests);
      when(interestRepository.save(any(Interest.class))).thenReturn(mockInterest);

      //when
      InterestDto result = interestService.createInterest(request);

      //then
      assertNotNull(result);
      assertEquals(request.name(), result.name());
      assertEquals(request.keywords(), result.keywords());
      assertEquals(0, result.subscriberCount());
      assertFalse(result.subscribedByMe());

      verify(interestRepository, times(1)).save(any(Interest.class));
    }

    @Test
    @DisplayName("실패: 80%이상 유사한 이름을 가진 관심사가 존재함")
    void createInterestFailure() {
      //given
      InterestCreateRequest request = new InterestCreateRequest(
          "예술/음악", List.of("클래식", "재즈", "힙합")
      );
      Interest mockInterest = new Interest(request.name(), request.keywords());

      when(interestRepository.findAll()).thenReturn(interests);

      //when & then
      assertThrows(InterestAlreadyExistsException.class,
          () -> interestService.createInterest(request));

      verify(interestRepository, times(0)).save(any(Interest.class));
    }
  }

  @Nested
  @DisplayName("관심사 물리 삭제")
  class DeleteInterest {

    @Test
    @DisplayName("성공")
    void deleteInterestSuccess() {
      //given
      UUID testData1Id = interests.get(0).getId();
      when(interestRepository.findById(testData1Id)).thenReturn(
          Optional.ofNullable(interests.get(0)));

      //when
      boolean isDeleted = interestService.deleteInterest(testData1Id);

      //then
      assertTrue(isDeleted);

      verify(interestRepository, times(1)).delete(interests.get(0));
    }

    @Test
    @DisplayName("실패: 해당 id를 가지는 관심사가 존재하지 않음")
    void deleteInterestFailure() {
      //given
      UUID randomId = UUID.randomUUID();
      when(interestRepository.findById(randomId)).thenReturn(Optional.empty());

      //when & then
      assertThrows(InterestNotFoundException.class,
          () -> interestService.deleteInterest(randomId));

      verify(interestRepository, never()).delete(any(Interest.class));
    }
  }

  @Nested
  @DisplayName("관심사 목록 조회")
  class GetInterest {

    @Test
    @DisplayName("성공: 검색어를 관심사 이름으로 포함하는 관심사가 있음(name asc)")
    void getInterestKeywordInNameOrderByNameAscSuccess() {
      // given
      String keyword = "프로그래";
      String orderBy = "name";
      String direction = "asc";
      UUID cursor = null;
      Instant after = null;
      int limit = 10;

      InterestSearchRequest interestSearchRequest = InterestSearchRequest.of(keyword, orderBy,
          direction, cursor, after, limit);

      PageRequest pageRequest = PageRequest.of(0, limit + 1);

      UUID userId = UUID.randomUUID();

      List<InterestSubscriptionInfoDto> searchResult = new ArrayList<>();

      Interest programing = interests.get(0); //프로그래밍

      InterestSubscriptionInfoDto interestSubscriptionInfoDto = new InterestSubscriptionInfoDto(
          programing,
          0L
      );

      searchResult.add(interestSubscriptionInfoDto);

      searchResult.sort(Comparator.comparing(dto -> dto.getInterest().getName()));

      when(interestRepository
          .getByNameOrKeywordsContaining(keyword, cursor, after, direction, orderBy, pageRequest))
          .thenReturn(searchResult);
      when(subscriptionRepository
          .existsByUserIdAndInterestId(userId, programing.getId()))
          .thenReturn(true);
      when(interestRepository.findById(programing.getId())).thenReturn(
          Optional.of(programing));

      // when
      CursorPageResponseDto<InterestDto> result = interestService.getInterestsWithSubscriberInfo(
          interestSearchRequest, userId);

      //then
      assertNotNull(result);
      assertEquals(1, searchResult.size());
      assertEquals(programing.getId(), result.nextCursor());
      assertEquals(programing.getCreatedAt(), result.nextAfter());
    }

    @Test
    @DisplayName("성공: 검색어를 키워드로 포함하는 관심사가 있음 (name desc)")
    void getInterestKeywordInKeywordsOrderByNameDescSuccess() {
      // given
      String keyword = "개발";
      String orderBy = "name";
      String direction = "desc";
      UUID cursor = null;
      Instant after = null;
      int limit = 10;

      InterestSearchRequest interestSearchRequest = InterestSearchRequest.of(keyword, orderBy,
          direction, cursor, after, limit);

      PageRequest pageRequest = PageRequest.of(0, limit + 1);

      UUID userId = UUID.randomUUID();

      List<InterestSubscriptionInfoDto> searchResult = new ArrayList<>();

      Interest programing = interests.get(0); //프로그래밍
      Interest develop = interests.get(3); //개발

      InterestSubscriptionInfoDto interestSubscriptionInfoDto1 = new InterestSubscriptionInfoDto(
          programing,
          1L
      );
      InterestSubscriptionInfoDto interestSubscriptionInfoDto2 = new InterestSubscriptionInfoDto(
          develop,
          0L
      );

      searchResult.add(interestSubscriptionInfoDto1);
      searchResult.add(interestSubscriptionInfoDto2);

      searchResult.sort(
          Comparator.comparing((InterestSubscriptionInfoDto dto) -> dto.getInterest().getName())
              .reversed());

      when(interestRepository
          .getByNameOrKeywordsContaining(keyword, cursor, after, direction, orderBy, pageRequest))
          .thenReturn(searchResult);
      when(interestRepository.findById(develop.getId())).thenReturn(
          Optional.of(develop));
      when(subscriptionRepository
          .existsByUserIdAndInterestId(userId, programing.getId())).thenReturn(true);
      when(subscriptionRepository
          .existsByUserIdAndInterestId(userId, develop.getId())).thenReturn(false);
      when(interestRepository.countByKeyword(keyword)).thenReturn(2);

      // when
      CursorPageResponseDto<InterestDto> result = interestService.getInterestsWithSubscriberInfo(
          interestSearchRequest, userId);

      //then
      assertNotNull(result);
      assertEquals(2, result.totalElements());
      assertEquals(develop.getId(), result.nextCursor());
      assertEquals(develop.getCreatedAt(), result.nextAfter());
    }

    @Test
    @DisplayName("성공: 검색어를 관심사 이름으로 포함하는 관심사가 있음(subscriberCount desc)")
    void getInterestKeywordInNameOrderBySubscriberCountDescSuccess() {
      // given
      String keyword = "프로그래";
      String orderBy = "subscriberCount";
      String direction = "desc";
      UUID cursor = null;
      Instant after = null;
      int limit = 10;

      InterestSearchRequest interestSearchRequest = InterestSearchRequest.of(keyword, orderBy,
          direction, cursor, after, limit);

      PageRequest pageRequest = PageRequest.of(0, limit + 1);

      UUID userId = UUID.randomUUID();

      Interest programing = interests.get(0); //프로그래밍

      List<InterestSubscriptionInfoDto> searchResult = new ArrayList<>();

      InterestSubscriptionInfoDto interestSubscriptionInfoDto1 = new InterestSubscriptionInfoDto(
          programing,
          1L
      );

      searchResult.add(interestSubscriptionInfoDto1);

      searchResult.sort(
          Comparator.comparingLong(InterestSubscriptionInfoDto::getSubscriberCount).reversed());

      when(interestRepository
          .getByNameOrKeywordsContaining(keyword, cursor, after, direction, orderBy, pageRequest))
          .thenReturn(searchResult);
      when(interestRepository.findById(programing.getId())).thenReturn(
          Optional.of(programing));
      when(subscriptionRepository
          .existsByUserIdAndInterestId(userId, programing.getId())).thenReturn(true);

      // when
      CursorPageResponseDto<InterestDto> result = interestService.getInterestsWithSubscriberInfo(
          interestSearchRequest, userId);

      //then
      assertNotNull(result);
      assertEquals(1, searchResult.size());
      assertEquals(programing.getId(), result.nextCursor());
      assertEquals(programing.getCreatedAt(), result.nextAfter());
    }

    @Test
    @DisplayName("성공: 검색어를 키워드로 포함하는 관심사가 있음 (subscriber asc)")
    void getInterestKeywordInKeywordsOrderBySubscriberCountAscSuccess() {
      // given
      String keyword = "개발";
      String orderBy = "subscriberCount";
      String direction = "asc";
      UUID cursor = null;
      Instant after = null;
      int limit = 1;

      InterestSearchRequest interestSearchRequest = InterestSearchRequest.of(keyword, orderBy,
          direction, cursor, after, limit);

      PageRequest pageRequest = PageRequest.of(0, limit + 1);

      UUID userId = UUID.randomUUID();

      Interest programing = interests.get(0);
      Interest develop = interests.get(3);

      List<InterestSubscriptionInfoDto> searchResult = new ArrayList<>();

      InterestSubscriptionInfoDto interestSubscriptionInfoDto1 = new InterestSubscriptionInfoDto(
          programing,
          1L
      );
      InterestSubscriptionInfoDto interestSubscriptionInfoDto2 = new InterestSubscriptionInfoDto(
          develop,
          0L
      );

      searchResult.add(interestSubscriptionInfoDto1);
      searchResult.add(interestSubscriptionInfoDto2);

      searchResult.sort(Comparator.comparingLong(InterestSubscriptionInfoDto::getSubscriberCount));

      when(interestRepository
          .getByNameOrKeywordsContaining(keyword, cursor, after, direction, orderBy, pageRequest))
          .thenReturn(searchResult);

      when(interestRepository.findById(develop.getId())).thenReturn(
          Optional.of(develop));

      when(subscriptionRepository
          .existsByUserIdAndInterestId(userId, develop.getId())).thenReturn(false);
      when(interestRepository.countByKeyword(keyword)).thenReturn(2);

      // when
      CursorPageResponseDto<InterestDto> result = interestService.getInterestsWithSubscriberInfo(
          interestSearchRequest, userId);

      //then
      assertNotNull(result);
      assertEquals(2, result.totalElements());
      assertEquals(develop.getId(), result.nextCursor());
      assertEquals(develop.getCreatedAt(), result.nextAfter());
    }

    @Test
    @DisplayName("성공: 조회 결과가 없음")
    void getInterestEmptySuccess() {
      // given
      String keyword = "뷰티";
      String orderBy = "name";
      String direction = "asc";
      UUID cursor = null;
      Instant after = null;
      int limit = 10;

      InterestSearchRequest interestSearchRequest = InterestSearchRequest.of(keyword, orderBy,
          direction, cursor, after, limit);

      UUID userId = UUID.randomUUID();

      when(interestRepository.countByKeyword(keyword)).thenReturn(0);

      // when
      CursorPageResponseDto<InterestDto> result = interestService.getInterestsWithSubscriberInfo(
          interestSearchRequest, userId);

      //then
      assertNotNull(result);
      assertEquals(0, result.content().size());
      assertNull(result.nextCursor());
      assertNull(result.nextAfter());
      assertEquals(0, result.totalElements());
      assertFalse(result.hasNext());
    }
  }


  @Nested
  @DisplayName("수정")
  class updateInterest {

    @Test
    @DisplayName("성공")
    void updateInterestSuccess() {
      //given
      UUID interestId = interests.get(1).getId();

      List<String> keywords = new ArrayList<>(interests.get(1).getKeywords());
      keywords.add("밴드");

      InterestUpdateRequest request = new InterestUpdateRequest(keywords);

      when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
      when(interestRepository.findById(interestId)).thenReturn(
          Optional.ofNullable(interests.get(1)));
      when(subscriptionRepository.countDistinctByInterestId(interestId)).thenReturn(2);
      when(subscriptionRepository.existsByUserIdAndInterestId(userId, interestId)).thenReturn(
          false);

      //when
      InterestDto interestDto = interestService.updateInterest(userId, interestId, request);

      //then
      assertNotNull(interestDto);
      assertEquals(interestDto.keywords(), keywords);

    }

    @Test
    @DisplayName("실패: 존재하지 않는 관심사 id")
    void updateInterestIdFailure() {

      UUID interestId = UUID.randomUUID();

      List<String> keywords = new ArrayList<>(interests.get(1).getKeywords());
      keywords.add("밴드");

      InterestUpdateRequest request = new InterestUpdateRequest(keywords);

      when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
      when(interestRepository.findById(interestId)).thenReturn(Optional.empty());

      //when & then
      assertThrows(InterestNotFoundException.class,
          () -> interestService.updateInterest(userId, interestId, request));

      assertNotEquals(keywords, interests.get(1).getKeywords());

      verify(interestRepository, never()).save(any(Interest.class));
    }

    @Test
    @DisplayName("실패: 키워드는 1개 이상이어야 한다")
    void updateInterestKeywordFailure() {
      //given
      UUID interestId = interests.get(1).getId();

      List<String> keywords = new ArrayList<>();

      InterestUpdateRequest request = new InterestUpdateRequest(keywords);

      //when & then
      assertThrows(EmptyKeywordsException.class,
          () -> interestService.updateInterest(userId, interestId, request));

      assertNotEquals(keywords, interests.get(1).getKeywords());

      verify(interestRepository, never()).save(any(Interest.class));
    }
  }

  @Nested
  @DisplayName("관심사 구독")
  class subscribe {

    @Test
    @DisplayName("성공")
    void subscribeSuccess() {
      //given
      Interest interest = interests.get(1);

      Subscription subscribe = new Subscription(user, interest);

      when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
      when(interestRepository.findById(interest.getId())).thenReturn(Optional.of(interest));
      when(subscriptionRepository.countDistinctByInterestId(interest.getId())).thenReturn(1);

      //when
      SubscriptionDto subscriptionDto = interestService.subscribeToInterest(interest.getId(),
          userId);

      //then
      assertNotNull(subscriptionDto);

      verify(subscriptionRepository, times(1)).save(any(Subscription.class));
    }

    @Test
    @DisplayName("실패: 존재하지 않는 관심사")
    void subscribeFailureSinceInterestId() {
      //given
      UUID interestId = UUID.randomUUID();

      when(interestRepository.findById(interestId)).thenReturn(Optional.empty());

      //when & then
      assertThrows(InterestNotFoundException.class,
          () -> interestService.subscribeToInterest(interestId, userId));

      verify(subscriptionRepository, never()).save(any(Subscription.class));
    }

    @Test
    @DisplayName("실패: 존재하지 않는 유저")
    void subscribeFailureSinceUserId() {
      //given
      Interest interest = interests.get(1);
      UUID userId = UUID.randomUUID();

      when(interestRepository.findById(interest.getId())).thenReturn(Optional.of(interest));
      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      //when & then
      assertThrows(UserNotFoundException.class,
          () -> interestService.subscribeToInterest(interest.getId(), userId));

      verify(subscriptionRepository, never()).save(any(Subscription.class));
    }
  }

  @Nested
  @DisplayName("관심사 구독 취소")
  class unsubscribe {

    @Test
    @DisplayName("성공")
    void unsubscribeSuccess() {
      //given
      Interest interest = interests.get(1);

      Subscription subscribe = new Subscription(user, interest);

      when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
      when(interestRepository.findById(interest.getId())).thenReturn(Optional.of(interest));
      when(subscriptionRepository.findByUserAndInterest(user, interest)).thenReturn(
          Optional.of(subscribe));

      //when & then
      assertTrue(interestService.unsubscribeFromInterest(interest.getId(), userId));

      verify(subscriptionRepository, times(1)).delete(any(Subscription.class));
    }

    @Test
    @DisplayName("실패: 존재하지 않는 관심사")
    void unsubscribeFailureSinceInterestId() {
      //given
      UUID interestId = UUID.randomUUID();

      when(interestRepository.findById(interestId)).thenReturn(Optional.empty());

      //when & then
      assertThrows(InterestNotFoundException.class,
          () -> interestService.unsubscribeFromInterest(interestId, userId));

      verify(subscriptionRepository, never()).delete(any(Subscription.class));
    }

    @Test
    @DisplayName("실패: 존재하지 않는 유저")
    void unsubscribeFailureSinceUserId() {
      //given
      Interest interest = interests.get(1);
      UUID userId = UUID.randomUUID();

      when(interestRepository.findById(interest.getId())).thenReturn(Optional.of(interest));
      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      //when & then
      assertThrows(UserNotFoundException.class,
          () -> interestService.unsubscribeFromInterest(interest.getId(), userId));

      verify(subscriptionRepository, never()).delete(any(Subscription.class));
    }

  }
}
