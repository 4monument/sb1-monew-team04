package com.sprint.monew.common.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("문자열 유사도 검사 클래스 테스트")
class SimilarityCalculatorTest {

  @Test
  @DisplayName("동일한 문자열")
  void calculate() {
    String str1 = "음악";
    String str2 = "음악";

    double v = SimilarityCalculator.calculateSimilarity(str1, str2);
    assertTrue(v == 1);
    System.out.println(v);
  }

  @Test
  @DisplayName("5글자중 한글자만 다른 문자열(80% 유사)")
  void calculate2() {
    String str1 = "프로그래밍";
    String str2 = "프로그래";

    double v = SimilarityCalculator.calculateSimilarity(str1, str2);
    assertTrue(v >= 0.8);
    System.out.println(v);
  }

  @Test
  @DisplayName("4글자중 한글자만 다른 문자열(75% 유사)")
  void calculate3() {
    String str1 = "음악예술";
    String str2 = "음악애술";

    double v = SimilarityCalculator.calculateSimilarity(str1, str2);
    assertTrue(v < 0.8);
    System.out.println(v);
  }

  @Test
  @DisplayName("모두 일치하지 않는 문자열")
  void calculateDistance() {

    String str1 = "음악";
    String str2 = "예술";

    double v = SimilarityCalculator.calculateSimilarity(str1, str2);
    assertTrue(v < 1);
    System.out.println(v);
  }


  @Test
  @DisplayName("50%만 일치하는 문자열")
  void calculateDifference() {

    String str1 = "음악/예술";
    String str2 = "예술";

    double v = SimilarityCalculator.calculateSimilarity(str1, str2);
    assertTrue(v == 0.5);
    System.out.println(v);
  }


  @Test
  @DisplayName("\"및\"으로 구분되어있고 단어 순서만 다른 동일한 관심사 ")
  void calculateDifference2() {

    String str1 = "음악 및 예술";
    String str2 = "예술 및 음악";

    double v = SimilarityCalculator.calculateSimilarity(str1, str2);
    assertTrue(v == 1);
    System.out.println(v);
  }

  @Test
  @DisplayName(" \"and\"로 구분되어있고, 대소문자와 단어 순서만 다른 동일한 관심사")
  void calculateDifference4() {

    String str1 = "Art And Music";
    String str2 = "music and art";

    double v = SimilarityCalculator.calculateSimilarity(str1, str2);
    assertTrue(v == 1);
    System.out.println(v);
  }
}