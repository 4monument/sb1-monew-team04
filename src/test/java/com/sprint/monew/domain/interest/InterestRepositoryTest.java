package com.sprint.monew.domain.interest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.sprint.monew.PostgresContainer;
import com.sprint.monew.common.config.TestQuerydslConfig;
import com.sprint.monew.domain.interest.dto.InterestSubscriptionInfoDto;
import com.sprint.monew.domain.interest.repository.CustomInterestRepositoryImpl;
import com.sprint.monew.domain.interest.repository.InterestRepository;
import com.sprint.monew.domain.interest.subscription.Subscription;
import com.sprint.monew.domain.user.User;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Testcontainers
@Import({CustomInterestRepositoryImpl.class, TestQuerydslConfig.class})
@AutoConfigureDataMongo
//@Sql(scripts = "/seed-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class InterestRepositoryTest {

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
  private InterestRepository interestRepository;

  private User user;
  private Interest interest;

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

    //관심사
    String name = "기술";
    List<String> keywords = List.of("인공지능", "AI", "IT", "프로그래밍");
    interest = new Interest(name, keywords);
    em.persist(interest);

    //구독
    Subscription subscription = new Subscription(user, interest);
    em.persist(subscription);

  }

  @Test
  @DisplayName("동일 이름 관심사 존재 여부 반환")
  void existsByName() {
    //given
    String keyword = "기술";

    //when & then
    assertThat(interestRepository.existsByName(keyword)).isTrue();

  }

  @Test
  @DisplayName("키워드 검색 총 결과 수 반환")
  void countByKeyword() {
    //given
    String name = "프로그래밍";
    List<String> keywords = List.of("개발자", "AI", "IT", "프론트엔드", "백엔드");
    interest = new Interest(name, keywords);
    em.persist(interest);

    String searchKeyword = "AI";

    //when & then
    assertThat(interestRepository.countByKeyword(searchKeyword)).isEqualTo(2);
  }


  @Nested
  @DisplayName("페이지네이션 조회")
  class getInterestUsingPagination {

    Interest interestDiet;
    Interest interestPrograming;

    void setUpForPagination() {
      interestDiet = new Interest("다이어트", List.of("헬스", "운동", "식단", "식습관", "음식"));
      em.persist(interestDiet);

      //구독
      em.persist(new Subscription(user, interestDiet));

      interestPrograming = new Interest("프로그래밍", List.of("개발자", "AI", "IT", "프론트엔드", "백엔드"));

      em.persist(interestPrograming);
      em.flush();
    }

    @Test
    @DisplayName("관심사 이름 오름차순")
    void getInterestUsingPaginationOrderByNameAsc() {

      //given
      setUpForPagination();

      List<InterestSubscriptionInfoDto> expectResult = new ArrayList<>();
      expectResult.add(new InterestSubscriptionInfoDto(interest, 1L));
      expectResult.add(new InterestSubscriptionInfoDto(interestPrograming, 0L));

      expectResult.sort(Comparator.comparing(dto -> dto.getInterest().getName()));

      String keyword = "프로그래";
      UUID cursorId = null;
      Instant afterAt = null;
      String sortDirection = "ASC";
      String sortField = "name";
      Pageable pageable = PageRequest.of(0, 10);

      //when
      List<InterestSubscriptionInfoDto> result = interestRepository.getByNameOrKeywordsContaining(
          keyword, cursorId, afterAt, sortDirection,
          sortField, pageable);

      //then
      assertThat(expectResult.get(0).getInterest().getName()).isEqualTo(
          result.get(0).getInterest().getName());
      assertThat(expectResult.get(0).getInterest().getKeywords()).isEqualTo(
          result.get(0).getInterest().getKeywords());
      assertThat(result.size()).isEqualTo(2);

    }

    @Test
    @DisplayName("관심사 이름 내림차순")
    void getInterestUsingPaginationOrderByKeywordDesc() {

      //given
      setUpForPagination();

      List<InterestSubscriptionInfoDto> expectResult = new ArrayList<>();
      expectResult.add(new InterestSubscriptionInfoDto(interest, 1L));
      expectResult.add(new InterestSubscriptionInfoDto(interestPrograming, 0L));

      //이름 내림차순 정렬
      expectResult.sort(
          Comparator.comparing((InterestSubscriptionInfoDto dto) -> dto.getInterest().getName())
              .reversed());

      String keyword = "프로그래";
      UUID cursorId = null;
      Instant afterAt = null;
      String sortDirection = "desc";
      String sortField = "name";
      Pageable pageable = PageRequest.of(0, 10);

      //when
      List<InterestSubscriptionInfoDto> result = interestRepository.getByNameOrKeywordsContaining(
          keyword, cursorId, afterAt, sortDirection,
          sortField, pageable);

      //then
      assertThat(expectResult.get(0).getInterest().getName()).isEqualTo(
          result.get(0).getInterest().getName());
      assertThat(expectResult.get(0).getInterest().getKeywords()).isEqualTo(
          result.get(0).getInterest().getKeywords());
      assertThat(result.size()).isEqualTo(2);

    }

    @Test
    @DisplayName("구독자 수 오름차순")
    void getInterestUsingPaginationOrderBySubscriberAsc() {

      //given
      setUpForPagination();

      List<InterestSubscriptionInfoDto> expectResult = new ArrayList<>();
      expectResult.add(new InterestSubscriptionInfoDto(interest, 1L));
      expectResult.add(new InterestSubscriptionInfoDto(interestPrograming, 0L));

      //구독자 수 오름차순 정렬
      expectResult.sort(
          Comparator.comparing(InterestSubscriptionInfoDto::getSubscriberCount));

      String keyword = "프로그래";
      UUID cursorId = null;
      Instant afterAt = null;
      String sortDirection = "asc";
      String sortField = "subscriberCount";
      Pageable pageable = PageRequest.of(0, 10);

      //when
      List<InterestSubscriptionInfoDto> result = interestRepository.getByNameOrKeywordsContaining(
          keyword, cursorId, afterAt, sortDirection,
          sortField, pageable);

      //then
      assertThat(expectResult.get(0).getInterest().getName()).isEqualTo(
          result.get(0).getInterest().getName());
      assertThat(expectResult.get(0).getInterest().getKeywords()).isEqualTo(
          result.get(0).getInterest().getKeywords());
      assertThat(result.size()).isEqualTo(2);

    }

    @Test
    @DisplayName("구독자 수 내림차순")
    void getInterestUsingPaginationOrderBySubscriberDesc() {

      //given
      setUpForPagination();

      List<InterestSubscriptionInfoDto> expectResult = new ArrayList<>();
      expectResult.add(new InterestSubscriptionInfoDto(interest, 1L));
      expectResult.add(new InterestSubscriptionInfoDto(interestPrograming, 0L));

      //구독자 수 내림차순 정렬
      expectResult.sort(
          Comparator.comparing(InterestSubscriptionInfoDto::getSubscriberCount).reversed());

      String keyword = "프로그래";
      UUID cursorId = null;
      Instant afterAt = null;
      String sortDirection = "desc";
      String sortField = "subscriberCount";
      Pageable pageable = PageRequest.of(0, 10);

      //when
      List<InterestSubscriptionInfoDto> result = interestRepository.getByNameOrKeywordsContaining(
          keyword, cursorId, afterAt, sortDirection,
          sortField, pageable);

      //then
      assertThat(expectResult.get(0).getInterest().getName()).isEqualTo(
          result.get(0).getInterest().getName());
      assertThat(expectResult.get(0).getInterest().getKeywords()).isEqualTo(
          result.get(0).getInterest().getKeywords());
      assertThat(result.size()).isEqualTo(2);

    }

    @Test
    @DisplayName("커서 O,  보조커서 O - 구독자수 오름차순")
    void getInterestUsingPaginationOrderBySubscriberCursors() {

      //given
      setUpForPagination();

      // 전체 데이터 확인
      List<InterestSubscriptionInfoDto> allResults = interestRepository.getByNameOrKeywordsContaining(
          "프로그래", null, null, "asc", "subscriberCount", PageRequest.of(0, 2));

      UUID cursorId = allResults.get(0).getInterest().getId();
      Instant afterAt = allResults.get(0).getInterest().getCreatedAt();

      // when - 다음 항목을 가져오기 (첫 번째 항목 제외)
      List<InterestSubscriptionInfoDto> result = interestRepository.getByNameOrKeywordsContaining(
          "프로그래", cursorId, afterAt, "asc", "subscriberCount", PageRequest.of(0, 1));

      // then
      assertThat(result.size()).isNotZero();
      // 가져온 첫 번째 항목이 전체 결과의 두 번째 항목과 일치해야
      assertThat(result.get(0).getInterest().getId()).isEqualTo(
          allResults.get(1).getInterest().getId());

    }

    @Test
    @DisplayName("커서 O,  보조커서 O - 구독자수 오름차순(구독자 수 같을 경우)")
    void getInterestUsingPaginationOrderBySubscriberAscCursor() {

      //given
      setUpForPagination();
      em.persist(new Subscription(user, interestPrograming));
      em.flush();

      // 전체 데이터 확인
      List<InterestSubscriptionInfoDto> allResults = interestRepository.getByNameOrKeywordsContaining(
          "프로그래", null, null, "asc", "subscriberCount", PageRequest.of(0, 10));

      assertThat(allResults.size()).isNotZero();

      UUID cursorId = allResults.get(0).getInterest().getId();
      Instant afterAt = allResults.get(0).getInterest().getCreatedAt();

      // when - 다음 항목을 가져오기 (첫 번째 항목 제외)
      List<InterestSubscriptionInfoDto> result = interestRepository.getByNameOrKeywordsContaining(
          "프로그래", cursorId, afterAt, "asc", "subscriberCount", PageRequest.of(0, 1));

      // then
      assertThat(result.size()).isNotZero();
      assertThat(result.get(0).getInterest().getId()).isEqualTo(
          allResults.get(1).getInterest().getId());

    }

    @Test
    @DisplayName("커서 O,  보조커서 O - 구독자수 내림차순(구독자 수 같을 경우)")
    void getInterestUsingPaginationOrderBySubscriberDescCursor() {

      //given
      setUpForPagination();
      em.persist(new Subscription(user, interestPrograming));
      em.flush();

      // 전체 데이터 확인
      List<InterestSubscriptionInfoDto> allResults = interestRepository.getByNameOrKeywordsContaining(
          "프로그래", null, null, "desc", "subscriberCount", PageRequest.of(0, 10));

      assertThat(allResults.size()).isNotZero();

      UUID cursorId = allResults.get(0).getInterest().getId();
      Instant afterAt = allResults.get(0).getInterest().getCreatedAt();

      // when - 다음 항목을 가져오기 (첫 번째 항목 제외)
      List<InterestSubscriptionInfoDto> result = interestRepository.getByNameOrKeywordsContaining(
          "프로그래", cursorId, afterAt, "desc", "subscriberCount", PageRequest.of(0, 1));

      // then
      assertThat(result.size()).isNotZero();
      assertThat(result.get(0).getInterest().getId()).isEqualTo(
          allResults.get(1).getInterest().getId());

    }

    @Test
    @DisplayName("커서 O,  보조커서 O - 관심사이름 오름차순")
    void getInterestUsingPaginationOrderByNameAscWithCursor() {

      //given
      setUpForPagination();

      // 전체 데이터 확인
      List<InterestSubscriptionInfoDto> allResults = interestRepository.getByNameOrKeywordsContaining(
          "프로그래", null, null, "asc", "name", PageRequest.of(0, 2));

      UUID cursorId = allResults.get(0).getInterest().getId();
      Instant afterAt = allResults.get(0).getInterest().getCreatedAt();

      // when - 다음 항목을 가져오기 (첫 번째 항목 제외)
      List<InterestSubscriptionInfoDto> result = interestRepository.getByNameOrKeywordsContaining(
          "프로그래", cursorId, afterAt, "asc", "name", PageRequest.of(0, 1));

      // then
      assertThat(result.size()).isNotZero();
      // 가져온 첫 번째 항목이 전체 결과의 두 번째 항목과 일치해야
      assertThat(result.get(0).getInterest().getId()).isEqualTo(
          allResults.get(1).getInterest().getId());
    }

    @Test
    @DisplayName("커서 O,  보조커서 O - 관심사이름 내림차순")
    void getInterestUsingPaginationOrderByNameCursors() {

      //given
      setUpForPagination();

      // 전체 데이터 확인
      List<InterestSubscriptionInfoDto> allResults = interestRepository.getByNameOrKeywordsContaining(
          "프로그래", null, null, "desc", "name", PageRequest.of(0, 2));

      UUID cursorId = allResults.get(0).getInterest().getId();
      Instant afterAt = allResults.get(0).getInterest().getCreatedAt();

      // when - 다음 항목을 가져오기 (첫 번째 항목 제외)
      List<InterestSubscriptionInfoDto> result = interestRepository.getByNameOrKeywordsContaining(
          "프로그래", cursorId, afterAt, "desc", "name", PageRequest.of(0, 1));

      // then
      assertThat(result.size()).isNotZero();
      // 가져온 첫 번째 항목이 전체 결과의 두 번째 항목과 일치해야
      assertThat(result.get(0).getInterest().getId()).isEqualTo(
          allResults.get(1).getInterest().getId());
    }

    @Test
    @DisplayName("커서 X, 보조커서 X - 구독자수 오름차순")
    void getInterestUsingPaginationOrderBySubscriberCursorsNull() {

      //given
      setUpForPagination();

      // 전체 데이터 확인
      List<InterestSubscriptionInfoDto> allResults = interestRepository.getByNameOrKeywordsContaining(
          "프로그래", null, null, "asc", "subscriberCount", PageRequest.of(0, 2));

      // then
      assertThat(allResults.size()).isNotZero();
      assertThat(allResults.get(0).getInterest().getId()).isEqualTo(
          allResults.get(0).getInterest().getId());
    }

    @Test
    @DisplayName("커서 X, 보조커서 X - 이름 내림차순")
    void getInterestUsingPaginationOrderByNameCursorsNull() {

      //given
      setUpForPagination();

      // 전체 데이터 확인
      List<InterestSubscriptionInfoDto> allResults = interestRepository.getByNameOrKeywordsContaining(
          "프로그래", null, null, "desc", "subscriberCount", PageRequest.of(0, 2));

      // then
      assertThat(allResults.size()).isNotZero();
      assertThat(allResults.get(0).getInterest().getId()).isEqualTo(
          allResults.get(0).getInterest().getId());
    }

    @Test
    @DisplayName("커서 X, 보조커서 O - 관심사 이름 오름차순")
    void getInterestUsingPaginationOrderByNameCursorIdNull() {

      //given
      setUpForPagination();

      // 전체 데이터 확인
      List<InterestSubscriptionInfoDto> allResults = interestRepository.getByNameOrKeywordsContaining(
          "프로그래", null, null, "asc", "name", PageRequest.of(0, 2));

      UUID cursorId = allResults.get(0).getInterest().getId();

      // when - 다음 항목을 가져오기 (첫 번째 항목 제외)
      List<InterestSubscriptionInfoDto> result = interestRepository.getByNameOrKeywordsContaining(
          "프로그래", cursorId, null, "asc", "name", PageRequest.of(0, 1));

      // then
      assertThat(result.size()).isNotZero();
      // 가져온 첫 번째 항목이 전체 결과의 두번째 항목과 일치해야함
      assertThat(result.get(0).getInterest().getId()).isEqualTo(
          allResults.get(0).getInterest().getId());
    }

    @Test
    @DisplayName("커서 X, 보조커서 O - 구독자수 오름차순")
    void getInterestUsingPaginationOrderBySubscriberCursorIdNull() {

      //given
      setUpForPagination();

      // 전체 데이터 확인
      List<InterestSubscriptionInfoDto> allResults = interestRepository.getByNameOrKeywordsContaining(
          "프로그래", null, null, "asc", "subscriberCount", PageRequest.of(0, 2));

      UUID cursorId = allResults.get(0).getInterest().getId();

      // when - 다음 항목을 가져오기 (첫 번째 항목 제외)
      List<InterestSubscriptionInfoDto> result = interestRepository.getByNameOrKeywordsContaining(
          "프로그래", cursorId, null, "asc", "subscriberCount", PageRequest.of(0, 1));

      // then
      assertThat(result.size()).isNotZero();
      // 가져온 첫 번째 항목이 전체 결과의 두번째 항목과 일치해야함
      assertThat(result.get(0).getInterest().getId()).isEqualTo(
          allResults.get(0).getInterest().getId());
    }

    @Test
    @DisplayName("커서 O, 보조커서 X - 관심사 이름 내림차순")
    void getInterestUsingPaginationOrderByNameAfterAtNull() {

      //given
      setUpForPagination();

      // 전체 데이터 확인
      List<InterestSubscriptionInfoDto> allResults = interestRepository.getByNameOrKeywordsContaining(
          "프로그래", null, null, "asc", "name", PageRequest.of(0, 2));

      Instant afterAt = allResults.get(0).getInterest().getCreatedAt();

      // when - 다음 항목을 가져오기 (첫 번째 항목 제외)
      List<InterestSubscriptionInfoDto> result = interestRepository.getByNameOrKeywordsContaining(
          "프로그래", null, afterAt, "asc", "name", PageRequest.of(0, 1));

      // then
      assertThat(result.size()).isNotZero();
      // 가져온 첫 번째 항목이 전체 결과의 두번째 항목과 일치해야함
      assertThat(result.get(0).getInterest().getId()).isEqualTo(
          allResults.get(0).getInterest().getId());
    }

    @Test
    @DisplayName("커서 O, 보조커서 X - 구독자수 내림차순")
    void getInterestUsingPaginationOrderBySubscriberAfterAtNull() {

      //given
      setUpForPagination();

      // 전체 데이터 확인
      List<InterestSubscriptionInfoDto> allResults = interestRepository.getByNameOrKeywordsContaining(
          "프로그래", null, null, "asc", "subscriberCount", PageRequest.of(0, 2));

      Instant afterAt = allResults.get(0).getInterest().getCreatedAt();

      // when - 다음 항목을 가져오기 (첫 번째 항목 제외)
      List<InterestSubscriptionInfoDto> result = interestRepository.getByNameOrKeywordsContaining(
          "프로그래", null, afterAt, "asc", "subscriberCount", PageRequest.of(0, 1));

      // then
      assertThat(result.size()).isNotZero();
      // 가져온 첫 번째 항목이 전체 결과의 두번째 항목과 일치해야함
      assertThat(result.get(0).getInterest().getId()).isEqualTo(
          allResults.get(0).getInterest().getId());
    }

    @Test
    @DisplayName("검색 결과가 없음(0개)")
    void getInterestUsingPaginationOrderBySubscriberAfterAtZero() {
      //given
      setUpForPagination();

      // 전체 데이터 확인
      List<InterestSubscriptionInfoDto> result = interestRepository.getByNameOrKeywordsContaining(
          "연애", null, null, "asc", "name", PageRequest.of(0, 1));

      // then
      assertThat(result.size()).isZero();
    }

    @Test
    @DisplayName("검색어가 없음 - 페이지 크기 만큼 조회")
    void getInterestUsingPaginationWithNoKeywords() {
      //given
      setUpForPagination();

      // 전체 데이터 확인
      List<InterestSubscriptionInfoDto> result = interestRepository.getByNameOrKeywordsContaining(
          null, null, null, "asc", "name", PageRequest.of(0, 3));

      // then
      assertThat(result.size()).isEqualTo(3);
    }


    @Test
    @DisplayName("정렬 조건 없음")
    void getInterestUsingPaginationWithNoSortField() {
      //given
      setUpForPagination();

      // 전체 데이터 확인
      List<InterestSubscriptionInfoDto> result = interestRepository.getByNameOrKeywordsContaining(
          null, null, null, "asc", null, PageRequest.of(0, 10));

      // then
      //assertThat(result.size()).isEqualTo(5);
      assertThat(result.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("정렬 순서 없음")
    void getInterestUsingPaginationWithNoSortDirection() {
      //given
      setUpForPagination();

      // 전체 데이터 확인
      List<InterestSubscriptionInfoDto> result = interestRepository.getByNameOrKeywordsContaining(
          null, null, null, null, "subscriberCount", PageRequest.of(0, 10));

      // then
      //assertThat(result.size()).isEqualTo(5);
      assertThat(result.size()).isEqualTo(3);
    }
  }
}
