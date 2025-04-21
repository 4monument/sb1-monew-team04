package com.sprint.monew.domain.interest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.monew.domain.interest.dto.InterestCreateRequest;
import com.sprint.monew.domain.interest.dto.InterestDto;
import com.sprint.monew.domain.interest.userinterest.UserInterestRepository;
import java.lang.reflect.Field;
import com.sprint.monew.common.util.CursorPageResponseDto;
import java.util.ArrayList;
import java.util.Arrays;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("관심사 서비스 테스트")
class InterestServiceTest {

  @Mock
  private InterestRepository interestRepository;

  @Mock
  private UserInterestRepository subscriptionRepository;

  @InjectMocks
  private InterestService interestService;

  // 테스트에 사용할 Interest 객체 리스트
  private List<Interest> interests;

  @BeforeEach
  void setUp() {
    interests = new ArrayList<>();

    // 첫 번째 테스트 데이터 - 프로그래밍 관심사
    Interest programming = new Interest("프로그래밍", Arrays.asList("Java", "Spring", "Python", "개발"));
    setPrivateField(programming, "id", UUID.fromString("a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d"));
    interests.add(programming);

    // 두 번째 테스트 데이터 - 음악 관심사
    Interest music = new Interest("음악", Arrays.asList("클래식", "재즈", "힙합"));
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
          "프로그래밍1", List.of("개발자", "기술", "개발", "AI")
      );
      Interest mockInterest = new Interest(request.name(), request.keywords());

      when(interestRepository.findAll()).thenReturn(interests);

      //when & then
      assertThrows(IllegalArgumentException.class, () -> interestService.createInterest(request));

      verify(interestRepository, times(0)).save(any(Interest.class));
    }
  }

  @Nested
  @DisplayName("관심사 삭제")
  class DeleteInterest {

    @Test
    @DisplayName("성공")
    void deleteInterestSuccess() {
      //given
      UUID testData1Id = interests.get(0).getId();
      when(interestRepository.findById(testData1Id)).thenReturn(
          Optional.ofNullable(interests.get(0)));

      //when
      boolean isDeleted = interestService.deleteInterest(testData1Id.toString());

      //then
      assertTrue(isDeleted);

      verify(interestRepository, times(1)).delete(interests.get(0));
    }

    @Test
    @DisplayName("실패")
    void deleteInterestFailure() {
      //given
      UUID randomId = UUID.randomUUID();
      when(interestRepository.findById(randomId)).thenReturn(Optional.empty());

      //when & then
      assertThrows(IllegalArgumentException.class,
          () -> interestService.deleteInterest(randomId.toString()));

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
      String cursor = null;
      String after = null;
      int limit = 10;
      UUID userId = UUID.randomUUID();

      List<Interest> searchResult = new ArrayList<>();
      searchResult.add(interests.get(0));
      UUID interestId = interests.get(0).getId();


      when(interestRepository
          .findByNameOrKeywordsContainingOrderByNameAsc(keyword, cursor, after, limit))
          .thenReturn(searchResult);
      when(interestRepository.findById(interestId)).thenReturn(Optional.ofNullable(interests.get(0)));
      when(subscriptionRepository
          .existsByUserIdAndInterestId(userId, interestId)).thenReturn(true);
      when(subscriptionRepository
          .countDistinctByInterestId(interestId)).thenReturn(1);

      // when
      CursorPageResponseDto<InterestDto> result = interestService.getInterests(
          keyword, orderBy, direction, cursor, after, limit, userId
      );

      //then
      assertNotNull(result);
      assertEquals(1, searchResult.size());
      assertEquals(searchResult.get(0).getId(), result.nextCursor());
      assertEquals(searchResult.get(0).getCreatedAt(), result.nextAfter());
    }

    @Test
    @DisplayName("성공: 검색어를 키워드로 포함하는 관심사가 있음 (name desc)")
    void getInterestKeywordInKeywordsOrderByNameDescSuccess() {
      // given
      String keyword = "개발";
      String orderBy = "name";
      String direction = "desc";
      String cursor = null;
      String after = null;
      int limit = 10;
      UUID userId = UUID.randomUUID();

      List<Interest> searchResult = new ArrayList<>();
      searchResult.add(interests.get(0));
      searchResult.add(interests.get(3));

      UUID interestId1 = interests.get(0).getId();
      UUID interestId2 = interests.get(3).getId();

      when(interestRepository
          .findByNameOrKeywordsContainingOrderByNameDesc(keyword, cursor, after, limit))
          .thenReturn(searchResult);
      when(interestRepository.findById(interestId2)).thenReturn(Optional.ofNullable(interests.get(3)));
      when(subscriptionRepository
          .existsByUserIdAndInterestId(userId, interestId1)).thenReturn(true);
      when(subscriptionRepository
          .existsByUserIdAndInterestId(userId, interestId2)).thenReturn(false);
      when(subscriptionRepository
          .countDistinctByInterestId(interestId1)).thenReturn(1);
      when(interestRepository.countByKeyword(keyword)).thenReturn(2);

      // when
      CursorPageResponseDto<InterestDto> result = interestService.getInterests(
          keyword, orderBy, direction, cursor, after, limit, userId
      );

      //then
      assertNotNull(result);
      assertEquals(2, result.totalElements());
      assertEquals(searchResult.get(1).getId(), result.nextCursor());
      assertEquals(searchResult.get(1).getCreatedAt(), result.nextAfter());
    }

    @Test
    @DisplayName("성공: 검색어를 관심사 이름으로 포함하는 관심사가 있음(subscriber desc)")
    void getInterestKeywordInNameOrderBySubscriberCountDescSuccess() {
      // given
      String keyword = "프로그래";
      String orderBy = "subscriberCount";
      String direction = "desc";
      String cursor = null;
      String after = null;
      int limit = 10;
      UUID userId = UUID.randomUUID();

      List<InterestWithSubscriberCount> searchResult = new ArrayList<>();
      Interest interest = interests.get(0);
      UUID interestId = interests.get(0).getId();

      searchResult.add(new TestInterestWithSubscriberCount(
          interest.getId(),
          interest.getName(),
          interest.getKeywords(),
          interest.getCreatedAt(),
          1L
      ));

      when(interestRepository
          .findByNameOrKeywordsContainingOrderBySubscriberCountDesc(keyword, cursor, after, limit))
          .thenReturn(searchResult);
      when(interestRepository.findById(interestId)).thenReturn(Optional.ofNullable(interests.get(0)));
      when(subscriptionRepository
          .existsByUserIdAndInterestId(userId, interestId)).thenReturn(true);

      // when
      CursorPageResponseDto<InterestDto> result = interestService.getInterests(
          keyword, orderBy, direction, cursor, after, limit, userId
      );

      //then
      assertNotNull(result);
      assertEquals(1, searchResult.size());
      assertEquals(searchResult.get(0).getId(), result.nextCursor());
      assertEquals(searchResult.get(0).getCreatedAt(), result.nextAfter());
    }

    @Test
    @DisplayName("성공: 검색어를 키워드로 포함하는 관심사가 있음 (subscriber asc)")
    void getInterestKeywordInKeywordsOrderBySubscriberCountAscSuccess() {
      // given
      String keyword = "개발";
      String orderBy = "subscriberCount";
      String direction = "asc";
      String cursor = null;
      String after = null;
      int limit = 10;
      UUID userId = UUID.randomUUID();

      List<InterestWithSubscriberCount> searchResult = new ArrayList<>();

      UUID interestId1 = interests.get(0).getId();
      UUID interestId2 = interests.get(3).getId();

      searchResult.add(new TestInterestWithSubscriberCount(
          interestId1,
          interests.get(0).getName(),
          interests.get(0).getKeywords(),
          interests.get(0).getCreatedAt(),
          1L
      ));
      searchResult.add(new TestInterestWithSubscriberCount(
          interestId2,
          interests.get(3).getName(),
          interests.get(3).getKeywords(),
          interests.get(3).getCreatedAt(),
          0L
      ));

      when(interestRepository
          .findByNameOrKeywordsContainingOrderBySubscriberCountAsc(keyword, cursor, after, limit))
          .thenReturn(searchResult);

      when(interestRepository.findById(interestId2)).thenReturn(Optional.ofNullable(interests.get(3)));

      when(subscriptionRepository
          .existsByUserIdAndInterestId(userId, interestId1)).thenReturn(true);
      when(subscriptionRepository
          .existsByUserIdAndInterestId(userId, interestId2)).thenReturn(false);
      when(interestRepository.countByKeyword(keyword)).thenReturn(2);

      // when
      CursorPageResponseDto<InterestDto> result = interestService.getInterests(
          keyword, orderBy, direction, cursor, after, limit, userId
      );

      //then
      assertNotNull(result);
      assertEquals(2, result.totalElements());
      assertEquals(searchResult.get(1).getId(), result.nextCursor());
      assertEquals(searchResult.get(1).getCreatedAt(), result.nextAfter());
    }

    @Test
    @DisplayName("성공: 조회 결과가 없음")
    void getInterestEmptySuccess() {
      // given
      String keyword = "뷰티";
      String orderBy = "name";
      String direction = "asc";
      String cursor = null;
      String after = null;
      int limit = 10;
      UUID userId = UUID.randomUUID();

      when(interestRepository.countByKeyword(keyword)).thenReturn(0);

      // when
      CursorPageResponseDto<InterestDto> result = interestService.getInterests(
          keyword, orderBy, direction, cursor, after, limit, userId
      );

      //then
      assertNotNull(result);
      assertEquals(0, result.content().size());
      assertNull(result.nextCursor());
      assertNull(result.nextAfter());
      assertEquals(0, result.totalElements());
      assertFalse(result.hasNext());
    }
  }
}