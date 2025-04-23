package com.sprint.monew.domain.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.monew.common.util.CursorPageResponseDto;
import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.Article.Source;
import com.sprint.monew.domain.interest.Interest;
import com.sprint.monew.domain.interest.subscription.SubscriptionRepository;
import com.sprint.monew.domain.notification.dto.UnreadInterestArticleCount;
import com.sprint.monew.domain.user.User;
import com.sprint.monew.domain.user.UserRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private SubscriptionRepository subscriberRepository;


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

  @Nested
  @DisplayName("알림 생성")
  class createNotificationTest {

    @Test
    @DisplayName("성공: 구독 중인 관심사 관련 기사 등록 시")
    void createNotificationInInterestSuccess() {
      //given
      Instant afterAt = Instant.now();
      Interest interest = interests.get(0);

      //기사 관심사 등록
      Article article = Article.create(
          Source.NAVER,
          "https://news.example.com/tech/2025/04/23/article12345",
          "인공지능 기술의 최신 동향과 미래 전망",
          Instant.parse("2025-04-22T15:30:00Z"),
          "최근 인공지능 기술의 발전과 산업 적용 사례를 분석하고, 향후 5년간의 기술 발전 방향을 예측한 보고서입니다."
      );

      //ArticleInterest 생성
      article.addInterest(interest);

      List<UnreadInterestArticleCount> queryResult = new ArrayList<>();
      queryResult.add(new TestUnreadInterestArticleCount(
          interest,
          user,
          1L
      ));

      when(subscriberRepository.findNewArticleCountWithUserInterest(afterAt))
          .thenReturn(queryResult);

      NotificationDto expectedNotification = NotificationDto.from(
          new Notification(user,
              interest.getId(),
              ResourceType.INTEREST,
              queryResult.get(0).getInterest().getName() + "와/과 관련된 기사가 "
                  + queryResult.get(0).getTotalNewArticles() + "건 등록되었습니다."));

      //when
      List<Notification> notifications = notificationService.createArticleInterestNotifications(
          afterAt);

      //then
      assertEquals(expectedNotification.userId(), notifications.get(0).getUser().getId());
      assertEquals(expectedNotification.resourceId(), notifications.get(0).getResourceId());
      assertEquals(expectedNotification.resourceType(),
          notifications.get(0).getResourceType().toString());
      assertEquals(expectedNotification.content(), notifications.get(0).getContent());
      assertFalse(expectedNotification.confirmed());
    }

  }

  @Nested
  @DisplayName("알림 조회")
  class getNotificationTest {

    @Test
    @DisplayName("성공: 다음 페이지 없음 (cursor null / afterAt null / limit 50)")
    void getNotificationHasNextFalseSuccess() {
      //given
      Interest interest = interests.get(0);

      UUID cursor = null;
      Instant afterAt = null;
      int limit = 50;
      UUID userId = user.getId();

      PageRequest pageRequest = PageRequest.of(0, limit + 1);

      List<Notification> expectNotifications = new ArrayList<>();

      Notification notification1 = new Notification(user, interests.get(0).getId(),
          ResourceType.INTEREST,
          interests.get(0).getName() + "와/과 관련된 기사가 " + 1 + "건 등록되었습니다.");

      Notification notification2 = new Notification(user, interests.get(1).getId(),
          ResourceType.INTEREST,
          interests.get(1).getName() + "와/과 관련된 기사가 " + 1 + "건 등록되었습니다.");

      expectNotifications.add(notification1);
      expectNotifications.add(notification2);

      when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
      when(notificationRepository
          .findUnconfirmedWithCursor(userId, cursor, afterAt, pageRequest))
          .thenReturn(expectNotifications);

      //when
      CursorPageResponseDto<NotificationDto> allNotifications
          = notificationService.getAllNotifications(null, null, limit, userId);

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

      allNotifications.content().forEach(notification ->
          assertFalse(notification.confirmed())
      );

    }

    @Test
    @DisplayName("성공: 다음 페이지 있음 (cursor null / afterAt null / limit 1)")
    void getNotificationHasNextTrueSuccess() {
      //given

      UUID cursor = null;
      Instant afterAt = null;
      int limit = 1;

      UUID userId = user.getId();

      PageRequest pageRequest = PageRequest.of(0, limit + 1);

      List<Notification> expectNotifications = new ArrayList<>();

      Notification notification1 = new Notification(user, interests.get(0).getId(),
          ResourceType.INTEREST,
          interests.get(0).getName() + "와/과 관련된 기사가 " + 1 + "건 등록되었습니다.");
      Notification notification2 = new Notification(user, interests.get(1).getId(),
          ResourceType.INTEREST,
          interests.get(1).getName() + "와/과 관련된 기사가 " + 1 + "건 등록되었습니다.");

      UUID notificationId1 = UUID.randomUUID();
      UUID notificationId2 = UUID.randomUUID();

      ReflectionTestUtils.setField(notification1, "id", notificationId1);
      ReflectionTestUtils.setField(notification2, "id", notificationId2);

      expectNotifications.add(notification1);
      expectNotifications.add(notification2);

      expectNotifications.sort(Comparator.comparing(Notification::getCreatedAt).reversed());

      when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
      when(notificationRepository
          .findUnconfirmedWithCursor(userId, cursor, afterAt, pageRequest))
          .thenReturn(expectNotifications);

      //when
      CursorPageResponseDto<NotificationDto> allNotifications
          = notificationService.getAllNotifications(null, null, limit, userId);

      //then
      assertEquals(notification2.getId(), allNotifications.nextCursor());
      assertEquals(notification2.getCreatedAt(), allNotifications.nextAfter());
      assertFalse(allNotifications.content().get(0).confirmed());
      assertTrue(allNotifications.hasNext());

    }

    @Test
    @DisplayName("성공: 다음 페이지 있고 커서 있음 (cursor not null / afterAt not null / limit 1)")
    void getNotificationHasNextTrueWithCursorSuccess() {
      //given
      int limit = 1;
      UUID userId = user.getId();

      PageRequest pageRequest = PageRequest.of(0, limit + 1);

      List<Notification> expectNotifications = new ArrayList<>();

      Notification notification1 = new Notification(user, interests.get(0).getId(),
          ResourceType.INTEREST,
          interests.get(0).getName() + "와/과 관련된 기사가 " + 1 + "건 등록되었습니다.");
      Notification notification2 = new Notification(user, interests.get(1).getId(),
          ResourceType.INTEREST,
          interests.get(1).getName() + "와/과 관련된 기사가 " + 1 + "건 등록되었습니다.");

      UUID notificationId1 = UUID.randomUUID();
      UUID notificationId2 = UUID.randomUUID();

      ReflectionTestUtils.setField(notification1, "id", notificationId1);
      ReflectionTestUtils.setField(notification2, "id", notificationId2);

      expectNotifications.add(notification1);
      expectNotifications.add(notification2);

      expectNotifications.sort(Comparator.comparing(Notification::getCreatedAt).reversed());
      expectNotifications.remove(expectNotifications.size() - 1 - limit);

      UUID cursor = notification2.getId();
      Instant afterAt = notification2.getCreatedAt();

      when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
      when(notificationRepository
          .findUnconfirmedWithCursor(userId, cursor, afterAt, pageRequest))
          .thenReturn(expectNotifications);

      //when
      CursorPageResponseDto<NotificationDto> allNotifications
          = notificationService.getAllNotifications(cursor, afterAt, limit, userId);

      //then
      assertEquals(notification1.getId(), allNotifications.nextCursor());
      assertEquals(notification1.getCreatedAt(), allNotifications.nextAfter());
      allNotifications.content().forEach(notification ->
          assertFalse(notification.confirmed())
      );
      assertFalse(allNotifications.hasNext());
    }
  }

  @Nested
  @DisplayName("알림 확인(수정)")
  class checkNotification {

    @Test
    @DisplayName("성공: 단일 확인")
    void checkNotificationSuccess() {
      //given
      String likedUserName = "테스트";
      UUID notificationId = UUID.randomUUID();
      UUID commentId = UUID.randomUUID();

      Notification notification = new Notification(user,
          commentId,
          ResourceType.COMMENT,
          likedUserName + "님이 나의 댓글을 좋아합니다."
      );
      ReflectionTestUtils.setField(notification, "id", notificationId);

      Instant initialUpdatedAt = notification.getUpdatedAt();

      when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
      when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

      //when
      notificationService.checkNotification(notification.getId(), user.getId());

      //then
      assertTrue(notification.isConfirmed());
      assertTrue(initialUpdatedAt.isBefore(notification.getUpdatedAt()));

      verify(notificationRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("실패: 단일 확인 - 유효하지 않은 알림 id")
    void checkNotificationByIdFailure() {
      //given
      UUID notificationId = UUID.randomUUID();

      when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
      when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

      //when & then
      assertThrows(IllegalArgumentException.class,
          () -> notificationService.checkNotification(notificationId, user.getId()));

      //then
      verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("실패: 단일 확인 - 유효하지 않은 유저 id")
    void checkNotificationForNoneFailure() {

      //given
      String likedUserName = "테스트";
      UUID userId = UUID.randomUUID();
      UUID notificationId = UUID.randomUUID();
      UUID commentId = UUID.randomUUID();

      Notification notification = new Notification(user,
          commentId,
          ResourceType.COMMENT,
          likedUserName + "님이 나의 댓글을 좋아합니다."
      );
      ReflectionTestUtils.setField(notification, "id", notificationId);

      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      //when & then
      assertThrows(IllegalArgumentException.class,
          () -> notificationService.checkNotification(notificationId, userId));

      //then
      verify(notificationRepository, never()).save(any());

    }

    @Test
    @DisplayName("성공: 전체 확인")
    void checkAllNotificationsSuccess() {
      //given
      UUID userId = user.getId();

      when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
      //when
      notificationService.checkAllNotifications(userId);

      //then
      verify(notificationRepository, times(1)).saveAll(any());
    }

    @Test
    @DisplayName("실패: 전체 확인 - 유효하지 않은 유저 id")
    void checkAllNotificationsFailure() {
      //given
      UUID userId = UUID.randomUUID();

      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      //when & then
      assertThrows(IllegalArgumentException.class,
          () -> notificationService.checkAllNotifications(userId));

      verify(notificationRepository, never()).saveAll(any());

    }
  }
}