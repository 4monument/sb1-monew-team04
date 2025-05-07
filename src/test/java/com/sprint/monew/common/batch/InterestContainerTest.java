package com.sprint.monew.common.batch;


import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.util.ReflectionTestUtils.*;

import com.sprint.monew.common.batch.support.InterestContainer;
import com.sprint.monew.domain.article.Article.Source;
import com.sprint.monew.domain.article.api.ArticleApiDto;
import com.sprint.monew.domain.interest.Interest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class InterestContainerTest {

  InterestContainer interestContainer = new InterestContainer();
  List<Interest> interests = new ArrayList<>();
  Set<String> keywords = new HashSet<>();
  List<String> sourceUrls = new ArrayList<>();

  @BeforeEach
  void setUp() {
    Interest programming = new Interest("프로그래밍", Arrays.asList("Java", "Spring", "Python", "개발"));
    Interest music = new Interest("음악/예술", Arrays.asList("클래식", "재즈", "힙합"));
    Interest travel = new Interest("여행", Arrays.asList("유럽", "아시아", "배낭여행", "맛집탐방"));
    Interest develop = new Interest("IT 기술", Arrays.asList("개발", "백엔드", "프론트엔드", "AI", "개발자"));
    interests.addAll(List.of(programming, music, travel, develop));
    keywords.addAll(programming.getKeywords());
    keywords.addAll(music.getKeywords());
    keywords.addAll(travel.getKeywords());
    keywords.addAll(develop.getKeywords());
    keywords = keywords.stream()
        .map(string -> string.toLowerCase().trim())
        .collect(Collectors.toSet());

    setField(programming, "id", UUID.randomUUID());
    setField(music, "id", UUID.randomUUID());
    setField(travel, "id", UUID.randomUUID());
    setField(develop, "id", UUID.randomUUID());

    sourceUrls = Arrays.asList(
        "https://www.naver.com/programming",
        "https://www.mnet.com/music",
        "https://www.traveler.com/travel",
        "https://www.codeit.com/develop"
    );
    interestContainer.register(interests, sourceUrls);
  }

  @Test
  @DisplayName("초기화 후 필드 테스트")
  void register() {
    // then
    List<Interest> savedInterests = (List<Interest>) getField(interestContainer, "interests");

    assertThat(savedInterests)
        .isEqualTo(interests)
        .hasSize(interests.size())
        .containsAll(interests);

    Set<String> savedKeywords = (Set<String>) getField(interestContainer, "keywords");
    assertThat(savedKeywords)
        .hasSize(keywords.size())
        .containsAll(keywords);

    Set<String> savedSourceUrls = (Set<String>) getField(interestContainer, "sourceUrlFilterSet");
    assertThat(savedSourceUrls)
        .hasSize(sourceUrls.size())
        .containsAll(sourceUrls);
  }

  @Test
  @DisplayName("InterestContainer의 필터링된 경우 - 키워드 미포함")
  void filterWithKeyword() {

    ArticleApiDto articleApiDto = new ArticleApiDto(
        Source.CHOSUN,
        "https://www.chosun.com/programming",
        "프로그래밍의 기초",
        Instant.now(),
        "글자 포함 안되는 문장"
    );

    // when
    ArticleApiDto filter = interestContainer.filter(articleApiDto);

    // then
    assertThat(filter)
        .as("summary가 keywords에 포함되지 않아 필터링된 기사")
        .isNull();
  }

  @Test
  @DisplayName("InterestContainer의 키워드 필터링된 경우 - URL이 겹치는 경우")
  void filterWithUrl() {

    // given
    ArticleApiDto articleApiDto = new ArticleApiDto(
        Source.NAVER,
        "https://www.naver.com/programming",
        "프로그래밍의 기초",
        Instant.now(),
        "개발자들에 대한 이야기"
    );

    // when
    ArticleApiDto filter = interestContainer.filter(articleApiDto);

    // then
    assertThat(filter)
        .as("원본 url이 겹쳐서 필터링된 기사")
        .isNull();
  }

  @Test
  @DisplayName("InterestContainer의 filtering을 견뎌낸 기사")
  void filterNot() {

    // given
    ArticleApiDto articleApiDto = new ArticleApiDto(
        Source.NAVER,
        "https://newUrl.com/newProgramming",
        "프로그래밍의 기초",
        Instant.now(),
        "개발+배낭여행 ..."
    );

    // when
    ArticleApiDto filter = interestContainer.filter(articleApiDto);

    // then
    assertThat(filter)
        .isEqualTo(articleApiDto);
  }
}
