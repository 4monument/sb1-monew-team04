package com.sprint.monew.domain.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import com.sprint.monew.domain.article.articleinterest.ArticleInterestRepository;
import com.sprint.monew.domain.interest.Interest;
import com.sprint.monew.domain.interest.userinterest.UserInterest;
import com.sprint.monew.domain.interest.userinterest.UserInterestRepository;
import com.sprint.monew.domain.notification.dto.UnreadInterestArticleCount;
import com.sprint.monew.domain.user.User;
import com.sprint.monew.domain.user.UserRepository;
import java.time.Instant;
import java.util.ArrayList;
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
class NotificationServiceTest {

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private UserInterestRepository subscriberRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ArticleInterestRepository articleInterestRepository;

  @InjectMocks
  private NotificationService notificationService;

  // 테스트용 Interest
  private List<Interest> interests;

  // 테스트용 User
  private User user;

  // 테스트용 UserInterest
  private UserInterest userInterest;

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

    //유저 관심사 등록(구독)
    userInterest = new UserInterest(user, interests.get(0));
  }

  @Nested
  @DisplayName("알림 생성")
  class createNotificationTest {

    @Test
    @DisplayName("성공: 구독 중인 관심사 관련 기사 등록 시")
    void createNotificationInInterestSuccess() {
      //given
      Interest interest = interests.get(0);

      List<UnreadInterestArticleCount> queryResult = new ArrayList<>();
      queryResult.add(new TestUnreadInterestArticleCount(
          interest.getId(),
          interest.getName(),
          1L
      ));

      when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
      when(subscriberRepository.countUnreadArticlesByInterest(user.getId()))
          .thenReturn(queryResult);

      NotificationDto expectedNotification = NotificationDto.from(
          new Notification(user,
              queryResult.get(0).getInterestId(),
              ResourceType.INTEREST,
              queryResult.get(0).getInterestName() + "와/과 관련된 기사가 "
                  + queryResult.get(0).getUnreadCount() + "건 등록되었습니다."));
      //when
      List<NotificationDto> notifications = notificationService.createNotifications(user.getId());

      //then
      assertEquals(expectedNotification.userId(), notifications.get(0).userId());
      assertEquals(expectedNotification.resourceId(), notifications.get(0).resourceId());
      assertEquals(expectedNotification.resourceType(), notifications.get(0).resourceType());
      assertEquals(expectedNotification.content(), notifications.get(0).content());
      assertFalse(expectedNotification.confirmed());
    }


    @Test
    @DisplayName("성공: 내가 작성한 댓글에 좋아요 눌릴 시")
    void createNotificationMyCommentSuccess() {

    }


  }

  @Nested
  @DisplayName("알림 수정")
  class updateNotificationTest {

  }

  @Nested
  @DisplayName("알림 삭제")
  class deleteNotificationTest {

  }

  @Nested
  @DisplayName("알림 조회")
  class getNotificationTest {

  }
}