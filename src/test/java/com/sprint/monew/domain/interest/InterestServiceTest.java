package com.sprint.monew.domain.interest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
class InterestServiceTest {

  @Mock
  private InterestRepository interestRepository;

  @InjectMocks
  private InterestService interestService;

  // 테스트에 사용할 Interest 객체 리스트
  private List<Interest> interests;

  @BeforeEach
  void setUp() {
    interests = new ArrayList<>();

    // 첫 번째 테스트 데이터 - 프로그래밍 관심사
    Interest programming = new Interest("프로그래밍", Arrays.asList("Java", "Spring", "Python"));
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
  class CreateInterest {
    @Test
    @DisplayName("관심사 등록 - 성공")
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
    @DisplayName("관심사 등록 - 실패")
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




}