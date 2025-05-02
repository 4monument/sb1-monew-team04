package com.sprint.monew.domain.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.monew.PostgresContainer;
import com.sprint.monew.common.config.TestQuerydslConfig;
import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.Article.Source;
import com.sprint.monew.domain.article.articleinterest.ArticleInterest;
import com.sprint.monew.domain.interest.Interest;
import com.sprint.monew.domain.interest.subscription.Subscription;
import com.sprint.monew.domain.user.User;
import jakarta.persistence.EntityManager;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Import({NotificationRepositoryCustomImpl.class, TestQuerydslConfig.class})
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureDataMongo
public class NotificationRepositoryTest {

  static final PostgresContainer postgresContainer = PostgresContainer.getInstance();

  static {
    postgresContainer.start();
  }

  @DynamicPropertySource
  static void overrideDataSourceProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgresContainer::getUsername);
    registry.add("spring.datasource.password", postgresContainer::getPassword);
  }

  @Autowired
  private EntityManager em;

  @Autowired
  private NotificationRepository notificationRepository;

  private User user;
  private Interest interest;
  private Notification notification;

  @BeforeEach
  void setUp() {
    //사용자
    String email = "test@example.com";
    String nickname = "테스트유저";
    String password = "test1234";
    Instant createdAt = Instant.now();
    boolean deleted = false;
    user = new User(email, nickname, password, createdAt, deleted);
    em.persist(user);

    //기사
    Article article = Article.create(Source.NAVER,
        "https://news.example.com/tech/2025/04/23/article12345", "인공지능 기술의 최신 동향과 미래 전망",
        Instant.parse("2025-04-22T15:30:00Z"),
        "최근 인공지능 기술의 발전과 산업 적용 사례를 분석하고, 향후 5년간의 기술 발전 방향을 예측한 보고서입니다.");
    em.persist(article);

    //관심사
    String name = "기술";
    List<String> keywords = List.of("인공지능", "AI", "IT");
    interest = new Interest(name, keywords);
    em.persist(interest);

    //기사-관심사
    ArticleInterest articleInterest = ArticleInterest.create(
        article, interest
    );
    em.persist(articleInterest);

    //구독
    Subscription subscription = new Subscription(user, interest);
    em.persist(subscription);

    //기존 알림 - 조회 및 삭제 테스트에 활용
    notification = notificationRepository.save(new Notification(
        user,
        UUID.randomUUID(), // 임의의 리소스 ID
        ResourceType.COMMENT,
        "누군가 내 댓글에 좋아요를 눌렀어요."
    ));
    em.persist(notification);
    em.flush();
  }


  @Test
  @DisplayName("미확인 알림 조회")
  void getUnconfirmedNotifications() {

    //given
    int limit = 10;
    PageRequest pageRequest = PageRequest.of(0, limit + 1);

    //when
    List<Notification> unconfirmedWithCursor = notificationRepository.getUnconfirmedWithCursor(
        user.getId(), null, null, pageRequest);

    //then
    assertEquals(1, unconfirmedWithCursor.size());
  }

  @Test
  @DisplayName("미확인 알림 총 개수 조회")
  void getUnconfirmedNotificationsCount() {
    //when
    int count = notificationRepository.countUnconfirmedByUserId(user.getId());
    assertEquals(1, count);
  }

  @Test
  @DisplayName("알림 읽음으로 수정 후 조회")
  void updateUnconfirmedNotifications() {
    //given
    notification.confirm(Instant.now());
    em.flush();

    //when
    int count = notificationRepository.countUnconfirmedByUserId(user.getId());

    //then
    assertEquals(0, count);
    assertEquals(0, notificationRepository.countUnconfirmedByUserId(user.getId()));

  }

  @Test
  @DisplayName("확인 시간으로부터 일주일 된 알림 삭제")
  void deleteUnconfirmedNotification() {
    //given - 알림 읽은 시각 7일 +1초 전으로 업데이트 변경
    notification.confirm(Instant.now().minus(Duration.ofDays(7).plusSeconds(1)));
    em.flush();

    //when
    notificationRepository.deleteConfirmedNotificationsOlderThan(Instant.now());

    //then
    assertEquals(0, notificationRepository.countUnconfirmedByUserId(user.getId()));
    assertTrue(notificationRepository.findById(notification.getId()).isEmpty());
  }

}