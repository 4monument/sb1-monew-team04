package com.sprint.monew.domain.activity;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.monew.PostgresContainer;
import com.sprint.monew.common.config.TestQuerydslConfig;
import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.Article.Source;
import com.sprint.monew.domain.article.articleview.ArticleView;
import com.sprint.monew.domain.comment.Comment;
import com.sprint.monew.domain.interest.Interest;
import com.sprint.monew.domain.interest.subscription.Subscription;
import com.sprint.monew.domain.comment.like.Like;
import com.sprint.monew.domain.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataJpaTest
@ExtendWith(SpringExtension.class)
@Import({UserActivityQueryRepository.class, TestQuerydslConfig.class})
class UserActivityQueryRepositoryTest {

  @Container
  static final PostgresContainer postgres = PostgresContainer.getInstance();

  @Autowired
  private EntityManager em;

  @Autowired
  private UserActivityQueryRepository repository;

  private UUID userId;

  @BeforeEach
  void setUp() {
    Instant now = Instant.now();

    // 유저 및 데이터 생성
    User user = new User(
        "test@email.com",
        "nickname",
        "password",
        now,
        false);
    em.persist(user);
    this.userId = user.getId();
    // 관심사
    Interest interest = new Interest(
        "경제",
        List.of(
            "금리",
            "물가"));
    em.persist(interest);

    // 유저 관심사 연결
    Subscription userInterest = new Subscription(
        user,
        interest);
    em.persist(userInterest);

    // 기사
    Article article = Article.create(
        Source.NAVER,
        "http://naver.com",
        "금리 인상",
        now,
        "금리 인상 요약");
    em.persist(article);

    // 댓글
    Comment comment = Comment.create(
        user,
        article,
        "좋은 기사네요");
    em.persist(comment);

    // 좋아요
    Like like = new Like(
        user,
        comment);
    em.persist(like);
    // 기사 조회
    ArticleView view = ArticleView.create(
        user,
        article);
    em.persist(view);

    em.flush();
    em.clear();
  }

  @Test
  @DisplayName("실패 - 존재하지 않는 유저 ID로 조회 시 null을 반환한다")
  void findUserActivity_shouldReturnNullForInvalidUser() {
    // given
    UUID invalidUserId = UUID.randomUUID();

    // when, then
    Assertions.assertThrows(
        EntityNotFoundException.class,
        () -> repository.findUserActivity(invalidUserId));
  }

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add(
        "spring.datasource.url",
        postgres::getJdbcUrl);
    registry.add(
        "spring.datasource.username",
        postgres::getUsername);
    registry.add(
        "spring.datasource.password",
        postgres::getPassword);

    registry.add(
        "spring.jpa.hibernate.ddl-auto",
        () -> "create");
  }
}
