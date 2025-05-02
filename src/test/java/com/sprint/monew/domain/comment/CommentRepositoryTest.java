package com.sprint.monew.domain.comment;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.monew.PostgresContainer;
import com.sprint.monew.common.config.TestQuerydslConfig;
import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.Article.Source;
import com.sprint.monew.domain.comment.dto.CommentDto;
import com.sprint.monew.domain.comment.dto.request.CommentRequest;
import com.sprint.monew.domain.comment.like.Like;
import com.sprint.monew.domain.comment.repository.CommentRepository;
import com.sprint.monew.domain.comment.repository.CommentRepositoryImpl;
import com.sprint.monew.domain.user.User;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataJpaTest
@ExtendWith(SpringExtension.class)
@Import({CommentRepositoryImpl.class, TestQuerydslConfig.class})
public class CommentRepositoryTest {

  @Container
  static final PostgresContainer postgres = PostgresContainer.getInstance();

  @Autowired
  EntityManager em;

  @Autowired
  CommentRepository commentRepository;

  @DynamicPropertySource
  static void overrideDataSourceProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  UUID userId;
  UUID articleId;
  UUID commentId1;
  Instant createdAtCursorComment2 = Instant.parse("2025-01-02T00:00:00Z");

  @BeforeEach
  void init() {
    User user = new User(
        "test@test.com",
        "testUser",
        "test",
        Instant.now(),
        false
    );

    Article article = Article.create(
        Source.NAVER, "http://test.com", "testArticle", Instant.now(), "test summary");

    Comment comment1 = Comment.create(user, article, "test comment1");
    Comment comment2 = Comment.create(user, article, "test comment2");
    Comment comment3 = Comment.create(user, article, "test comment3");
    Comment comment4 = Comment.create(user, article, "test comment4");
    Comment comment5 = Comment.create(user, article, "test comment5");
    ReflectionTestUtils.setField(comment1, "createdAt", createdAtCursorComment2.minusSeconds(1000));
    ReflectionTestUtils.setField(comment2, "createdAt", createdAtCursorComment2);
    ReflectionTestUtils.setField(comment3, "createdAt", createdAtCursorComment2.plusSeconds(1000));
    ReflectionTestUtils.setField(comment4, "createdAt", createdAtCursorComment2.plusSeconds(2000));
    ReflectionTestUtils.setField(comment5, "createdAt", createdAtCursorComment2.plusSeconds(3000));

    Like like1 = new Like(user, comment2);
    Like like2 = new Like(user, comment4);

    em.persist(user);
    em.persist(article);
    em.persist(comment1);
    em.persist(comment2);
    em.persist(comment3);
    em.persist(comment4);
    em.persist(comment5);
    em.persist(like1);
    em.persist(like2);

    this.userId = user.getId();
    this.articleId = article.getId();
    this.commentId1 = comment1.getId();

    em.flush();
    em.clear();
  }

  @Test
  @DisplayName("cursor가 null이고 createdAt으로 내림차순 정렬")
  void getComments_sortByCreatedAtDescWithoutCursor() {
    CommentRequest request = new CommentRequest(articleId, null, null);
    PageRequest pageRequest = PageRequest.of(0, 3, Direction.DESC, "createdAt");

    Slice<CommentDto> page = commentRepository.getComments(request, userId, pageRequest);

    assertThat(page.hasNext()).isTrue();
    assertThat(page.getSize()).isEqualTo(3);

    List<CommentDto> content = page.getContent();
    assertThat(content.size()).isEqualTo(3);
    assertThat(content).extracting("content")
        .containsExactly("test comment5", "test comment4", "test comment3");
  }

  @Test
  @DisplayName("cursor가 null이고 createdAt으로 오름차순 정렬")
  void getComments_sortByCreatedAtAscWithoutCursor() {
    CommentRequest request = new CommentRequest(articleId, null, null);
    PageRequest pageRequest = PageRequest.of(0, 3, Direction.ASC, "createdAt");

    Slice<CommentDto> page = commentRepository.getComments(request, userId, pageRequest);

    assertThat(page.hasNext()).isTrue();
    assertThat(page.getSize()).isEqualTo(3);

    List<CommentDto> content = page.getContent();
    assertThat(content.size()).isEqualTo(3);
    assertThat(content).extracting("content")
        .containsExactly("test comment1", "test comment2", "test comment3");
  }

  @Test
  @DisplayName("cursor가 null이고 likeCount로 내림차순 정렬")
  void getComments_sortByLikeCountDescWithoutCursor() {
    CommentRequest request = new CommentRequest(articleId, null, null);
    PageRequest pageRequest = PageRequest.of(0, 3, Direction.DESC, "likeCount");

    Slice<CommentDto> page = commentRepository.getComments(request, userId, pageRequest);


    assertThat(page.hasNext()).isTrue();
    assertThat(page.getSize()).isEqualTo(3);

    List<CommentDto> content = page.getContent();
    assertThat(content.size()).isEqualTo(3);
    assertThat(content).extracting("content")
        .containsExactly("test comment4", "test comment2", "test comment5");
  }

  @Test
  @DisplayName("cursor가 null이고 likeCount로 오름차순 정렬")
  void getComments_sortByLikeCountAscWithoutCursor() {
    CommentRequest request = new CommentRequest(articleId, null, null);
    PageRequest pageRequest = PageRequest.of(0, 3, Direction.ASC, "likeCount");

    Slice<CommentDto> page = commentRepository.getComments(request, userId, pageRequest);

    assertThat(page.hasNext()).isTrue();
    assertThat(page.getSize()).isEqualTo(3);

    List<CommentDto> content = page.getContent();
    assertThat(content.size()).isEqualTo(3);
    assertThat(content).extracting("content")
        .containsExactly("test comment5", "test comment3", "test comment1");
  }

  @Test
  @DisplayName("cursor가 createdAt이고 내림차순 정렬")
  void getComments_sortByCreatedAtDesc() {
    CommentRequest request = new CommentRequest(articleId, createdAtCursorComment2.toString(),
        createdAtCursorComment2);
    PageRequest pageRequest = PageRequest.of(0, 3, Direction.DESC, "createdAt");

    Slice<CommentDto> page = commentRepository.getComments(request, userId, pageRequest);

    assertThat(page.hasNext()).isFalse();
    assertThat(page.getSize()).isEqualTo(3);

    List<CommentDto> content = page.getContent();
    assertThat(content.size()).isEqualTo(1);
    assertThat(content).extracting("content")
        .containsExactly("test comment1");
  }

  @Test
  @DisplayName("cursor가 createdAt이고 오름차순 정렬")
  void getComments_sortByCreatedAtAsc() {
    CommentRequest request = new CommentRequest(articleId, createdAtCursorComment2.toString(),
        createdAtCursorComment2);
    PageRequest pageRequest = PageRequest.of(0, 3, Direction.ASC, "createdAt");

    Slice<CommentDto> page = commentRepository.getComments(request, userId, pageRequest);

    assertThat(page.hasNext()).isFalse();
    assertThat(page.getSize()).isEqualTo(3);

    List<CommentDto> content = page.getContent();
    assertThat(content.size()).isEqualTo(3);
    assertThat(content).extracting("content")
        .containsExactly("test comment3", "test comment4", "test comment5");
  }

}
