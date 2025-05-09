package com.sprint.monew.domain.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import com.sprint.monew.common.batch.support.NotificationJdbc;
import com.sprint.monew.common.util.CursorPageResponseDto;
import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.Article.Source;
import com.sprint.monew.domain.interest.Interest;
import com.sprint.monew.domain.notification.dto.NotificationDto;
import com.sprint.monew.domain.notification.dto.NotificationSearchRequest;
import com.sprint.monew.domain.notification.dto.UnreadInterestArticleCount;
import com.sprint.monew.domain.notification.exception.NotificationNotFoundException;
import com.sprint.monew.domain.notification.repository.NotificationRepository;
import com.sprint.monew.domain.user.User;
import com.sprint.monew.domain.user.UserRepository;
import com.sprint.monew.domain.user.exception.UserNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
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
@DisplayName("알림 서비스 테스트")
class NotificationServiceTest {

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private NotificationService notificationService;

  // 테스트용 Interest
  private List<Interest> interests;

  // 테스트용 User
  private User user;

  @BeforeEach
  void setUp() {
    UUID id = UUID.randomUUID();
    String email = "test@example.com";
    String nickname = "테스트유저";
    String password = "test1234";
    Instant createdAt = Instant.now();
    boolean deleted = false;
    user = new User(id, email, nickname, password, createdAt, deleted);

    interests = new ArrayList<>();

    String name = "경제/투자";
    List<String> keywords = List.of("주식", "투자", "경제지표", "부동산", "금융시장");
    interests.add(new Interest(name, keywords));

    String healthName = "건강/웰빙";
    List<String> healthKeywords = List.of("다이어트", "운동", "영양", "명상", "웰니스");
    interests.add(new Interest(healthName, healthKeywords));

  }

  @Test
  @DisplayName("알림 생성")
  void createNotificationSuccess() {
    Instant afterAt = Instant.now();
    Interest interest = interests.get(0);

    //기사 관심사 등록
    Article article = Article.create(Source.NAVER,
        "https://news.example.com/tech/2025/04/23/article12345", "인공지능 기술의 최신 동향과 미래 전망",
        Instant.parse("2025-04-22T15:30:00Z"),
        "최근 인공지능 기술의 발전과 산업 적용 사례를 분석하고, 향후 5년간의 기술 발전 방향을 예측한 보고서입니다.");

    //ArticleInterest 생성
    article.addInterest(interest);

    UnreadInterestArticleCount data = new TestUnreadInterestArticleCount(interest, user, 1L);

    NotificationJdbc expectResult = NotificationJdbc.create(data);

    //when
    NotificationJdbc articleInterestNotification = notificationService.createArticleInterestNotifications(
        data);

    //then
    assertEquals(expectResult.userId(), articleInterestNotification.userId());
    assertEquals(expectResult.resourceId(), articleInterestNotification.resourceId());
    assertEquals(ResourceType.INTEREST, articleInterestNotification.resourceType());
    assertFalse(articleInterestNotification.confirmed());

  }

  @Nested
  @DisplayName("알림 전체 확인(수정)")
  class checkAllNotificationTest {

    @Test
    @DisplayName("성공")
    void checkAllNotificationSuccess() {
      //given
      UUID userId = user.getId();

      List<Notification> notifications = new ArrayList<>();

      Notification notification1 = new Notification(user, interests.get(0).getId(),
          ResourceType.INTEREST,
          interests.get(0).getName() + "와/과 관련된 기사가 " + 1 + "건 등록되었습니다.");

      Notification notification2 = new Notification(user, interests.get(1).getId(),
          ResourceType.INTEREST,
          interests.get(1).getName() + "와/과 관련된 기사가 " + 1 + "건 등록되었습니다.");

      Notification notification3 = new Notification(user, interests.get(0).getId(),
          ResourceType.COMMENT,
          user.getNickname() + "님이 나의 댓글을 좋아합니다.");

      notifications.add(notification1);
      notifications.add(notification2);
      notifications.add(notification3);

      when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
      when(notificationRepository.findByUser(user)).thenReturn(notifications);

      //when
      notificationService.checkAllNotifications(userId);

      //then
      assertTrue(notification1.isConfirmed());
      assertTrue(notification2.isConfirmed());
      assertTrue(notification3.isConfirmed());

      verify(notificationRepository, times(1)).saveAll(notifications);
    }

    @Test
    @DisplayName("실패: 해당 ID를 가진 사용자가 없음")
    void checkAllNotificationFailure() {
      //given
      UUID userId = UUID.randomUUID();

      List<Notification> notifications = new ArrayList<>();

      Notification notification1 = new Notification(user, interests.get(0).getId(),
          ResourceType.INTEREST,
          interests.get(0).getName() + "와/과 관련된 기사가 " + 1 + "건 등록되었습니다.");

      Notification notification2 = new Notification(user, interests.get(0).getId(),
          ResourceType.INTEREST,
          interests.get(0).getName() + "와/과 관련된 기사가 " + 1 + "건 등록되었습니다.");

      Notification notification3 = new Notification(user, interests.get(0).getId(),
          ResourceType.COMMENT,
          user.getNickname() + "님이 나의 댓글을 좋아합니다.");

      notifications.add(notification1);
      notifications.add(notification2);
      notifications.add(notification3);

      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      //when & then
      assertThrows(UserNotFoundException.class,
          () -> notificationService.checkAllNotifications(userId));

      //then
      assertFalse(notification1.isConfirmed());
      assertFalse(notification2.isConfirmed());
      assertFalse(notification3.isConfirmed());

      verify(notificationRepository, never()).saveAll(notifications);
    }
  }

  @Nested
  @DisplayName("알림 단일 확인(수정)")
  class checkNotificationTest {

    @Test
    @DisplayName("성공")
    void checkNotificationSuccess() {
      //given
      Interest interest = interests.get(0);
      UUID userId = user.getId();
      UUID notificationId = UUID.randomUUID();

      Notification notification = new Notification(user, interest.getId(), ResourceType.INTEREST,
          interest.getName() + "와/과 관련된 기사가 " + 1 + "건 등록되었습니다.");

      when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
      when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

      //when
      notificationService.checkNotification(notificationId, userId);

      //then
      assertTrue(notification.isConfirmed());

      verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    @DisplayName("실패: 해당 ID를 가진 사용자가 없음")
    void checkNotificationFailureSinceUserId() {
      //given
      Interest interest = interests.get(0);
      UUID userId = UUID.randomUUID();
      UUID notificationId = UUID.randomUUID();

      Notification notification = new Notification(user, interest.getId(), ResourceType.INTEREST,
          interest.getName() + "와/과 관련된 기사가 " + 1 + "건 등록되었습니다.");

      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      //when & then
      assertThrows(UserNotFoundException.class,
          () -> notificationService.checkNotification(notificationId, userId));

      assertFalse(notification.isConfirmed());

      verify(notificationRepository, never()).save(notification);
    }

    @Test
    @DisplayName("실패: 해당 ID를 가진 알림이 없음")
    void checkNotificationFailureSinceNotificationId() {
      //given
      Interest interest = interests.get(0);
      UUID userId = UUID.randomUUID();
      UUID notificationId = UUID.randomUUID();

      Notification notification = new Notification(user, interest.getId(), ResourceType.INTEREST,
          interest.getName() + "와/과 관련된 기사가 " + 1 + "건 등록되었습니다.");

      when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
      when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

      //when & then
      assertThrows(NotificationNotFoundException.class,
          () -> notificationService.checkNotification(notificationId, userId));

      assertFalse(notification.isConfirmed());

      verify(notificationRepository, never()).save(notification);
    }
  }

  @Nested
  @DisplayName("알림 조회")
  class getNotificationTest {

    @Test
    @DisplayName("성공: 새로운 알림이 없음")
    void getNotificationSuccess() {
      //given

      NotificationSearchRequest request = new NotificationSearchRequest(null, null, 50);
      UUID userId = user.getId();

      PageRequest pageRequest = PageRequest.of(0, request.limit() + 1);

      when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
      when(
          notificationRepository.getUnconfirmedWithCursor(userId, request.cursor(), request.after(),
              pageRequest)).thenReturn(List.of());

      //when
      CursorPageResponseDto<NotificationDto> results = notificationService.getAllNotifications(
          request, userId);

      //then
      assertEquals(0, results.content().size());
      assertNull(results.nextCursor());
      assertNull(results.nextAfter());
      assertEquals(0, results.size());
      assertEquals(0, results.totalElements());
    }

    @Test
    @DisplayName("성공: 다음 페이지 없음 (cursor null / afterAt null / limit 50)")
    void getNotificationHasNextFalseSuccess() {
      //given
      Interest interest = interests.get(0);

      NotificationSearchRequest request = new NotificationSearchRequest(null, null, 50);

      UUID userId = user.getId();

      PageRequest pageRequest = PageRequest.of(0, request.limit() + 1);

      List<Notification> expectNotifications = new ArrayList<>();
      expectNotifications.add(new Notification(user, interest.getId(), ResourceType.INTEREST,
          interest.getName() + "와/과 관련된 기사가 " + 1 + "건 등록되었습니다."));

      when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
      when(
          notificationRepository.getUnconfirmedWithCursor(userId, request.cursor(), request.after(),
              pageRequest)).thenReturn(expectNotifications);

      //when
      CursorPageResponseDto<NotificationDto> allNotifications = notificationService.getAllNotifications(
          request, userId);

      //then
      assertEquals(expectNotifications.get(0).getId(), allNotifications.content().get(0).id());

      assertEquals(expectNotifications.get(0).getUser().getId(),
          allNotifications.content().get(0).userId());

      assertEquals(expectNotifications.get(0).getResourceId(),
          allNotifications.content().get(0).resourceId());

      assertEquals(expectNotifications.get(0).getResourceType().toString(),
          allNotifications.content().get(0).resourceType());

      assertEquals(expectNotifications.get(0).getContent(),
          allNotifications.content().get(0).content());

    }

    @Test
    @DisplayName("성공: 다음 페이지 있음 (cursor null / afterAt null / limit 1)")
    void getNotificationHasNextTrueSuccess() {
      //given

      NotificationSearchRequest request = new NotificationSearchRequest(null, null, 1);

      UUID userId = user.getId();

      PageRequest pageRequest = PageRequest.of(0, request.limit() + 1);

      List<Notification> expectNotifications = new ArrayList<>();

      Notification notification1 = new Notification(user, interests.get(0).getId(),
          ResourceType.INTEREST, interests.get(0).getName() + "와/과 관련된 기사가 " + 1 + "건 등록되었습니다.");
      Notification notification2 = new Notification(user, interests.get(1).getId(),
          ResourceType.INTEREST, interests.get(1).getName() + "와/과 관련된 기사가 " + 1 + "건 등록되었습니다.");

      notification1.setCreatedAt(Instant.now());
      notification1.setUpdatedAt(notification1.getCreatedAt());

      notification2.setCreatedAt(Instant.now().plus(Duration.ofSeconds(1)));
      notification2.setUpdatedAt(notification2.getCreatedAt());

      UUID notificationId1 = UUID.randomUUID();
      UUID notificationId2 = UUID.randomUUID();

      setField(notification1, "id", notificationId1);
      setField(notification2, "id", notificationId2);

      expectNotifications.add(notification1);
      expectNotifications.add(notification2);

      expectNotifications.sort(Comparator.comparing(Notification::getCreatedAt).reversed());

      when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
      when(
          notificationRepository.getUnconfirmedWithCursor(userId, request.cursor(), request.after(),
              pageRequest)).thenReturn(expectNotifications);

      //when
      CursorPageResponseDto<NotificationDto> allNotifications = notificationService.getAllNotifications(
          request, userId);

      //then
      assertEquals(notification2.getId(), allNotifications.nextCursor());
      assertEquals(notification2.getCreatedAt(), allNotifications.nextAfter());
      assertTrue(allNotifications.hasNext());

    }

    @Test
    @DisplayName("성공: 다음 페이지 있고 커서 없음 (cursor not null / afterAt not null / limit 1)")
    void getNotificationHasNextTrueWithCursorSuccess() {
      //given
      UUID userId = user.getId();

      List<Notification> expectNotifications = new ArrayList<>();

      Notification notification1 = new Notification(user, interests.get(0).getId(),
          ResourceType.INTEREST, interests.get(0).getName() + "와/과 관련된 기사가 " + 1 + "건 등록되었습니다.");
      Notification notification2 = new Notification(user, interests.get(1).getId(),
          ResourceType.INTEREST, interests.get(1).getName() + "와/과 관련된 기사가 " + 1 + "건 등록되었습니다.");

      UUID notificationId1 = UUID.randomUUID();
      UUID notificationId2 = UUID.randomUUID();

      notification1.setCreatedAt(Instant.now());
      notification1.setUpdatedAt(notification1.getCreatedAt());

      notification2.setCreatedAt(Instant.now().plus(Duration.ofSeconds(1)));
      notification2.setUpdatedAt(notification2.getCreatedAt());

      setField(notification1, "id", notificationId1);
      setField(notification2, "id", notificationId2);

      UUID cursor = notification2.getId();
      Instant afterAt = notification2.getCreatedAt();

      NotificationSearchRequest request = new NotificationSearchRequest(cursor, afterAt, 1);
      PageRequest pageRequest = PageRequest.of(0, request.limit() + 1);

      expectNotifications.add(notification1);
      expectNotifications.add(notification2);

      expectNotifications.sort(Comparator.comparing(Notification::getCreatedAt).reversed());
      expectNotifications.remove(expectNotifications.size() - 1 - request.limit());

      when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
      when(notificationRepository.getUnconfirmedWithCursor(userId, cursor, afterAt,
          pageRequest)).thenReturn(expectNotifications);

      //when
      CursorPageResponseDto<NotificationDto> allNotifications = notificationService.getAllNotifications(
          request, userId);

      //then
      assertEquals(notification1.getId(), allNotifications.nextCursor());
      assertEquals(notification1.getCreatedAt(), allNotifications.nextAfter());
      assertFalse(allNotifications.hasNext());

    }
  }
}
