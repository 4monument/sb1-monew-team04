package com.sprint.monew.common.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SimilarityCalculator {

  //두 문자열 간 유사도 검사
  public static double calculateSimilarity(String str1, String str2) {
    if (str1.equals(str2)) {
      return 1.0;
    }

    if (str1 == null || str2 == null || str1.isEmpty() || str2.isEmpty()) {
      return 0.0;
    }

    // 대소문자 구분 없애기
    str1 = str1.toLowerCase();
    str2 = str2.toLowerCase();

    // 특수문자와 공백을 기준으로 단어 분리
    String[] words1 = str1.split("[\\s\\p{Punct}및]+");
    String[] words2 = str2.split("[\\s\\p{Punct}및]+");

    // 빈 문자열 제거
    words1 = removeEmptyStrings(words1);
    words2 = removeEmptyStrings(words2);

    // 분리된 단어가 있는지 확인
    boolean isMultipleWords1 = words1.length > 1;
    boolean isMultipleWords2 = words2.length > 1;

    // 둘 중 하나라도 여러 단어로 분리된 경우 -> 자카드 유사도
    if (isMultipleWords1 || isMultipleWords2) {
      return calculateJaccardSimilarity(words1, words2);
    }
    // 둘 다 단일 단어인 경우 -> 레벤슈타인 거리
    else {
      return levenshteinDistance(str1, str2);
    }
  }

  // 빈 문자열 제거하는 메서드
  private static String[] removeEmptyStrings(String[] arr) {
    return Arrays.stream(arr)
        .filter(s -> !s.isEmpty())
        .toArray(String[]::new);
  }

  /**
   * 레벤슈타인 거리를 계산 한 문자열에서 다른 문자열로 변환하는 데 필요한 최소 편집 연산(삽입, 삭제, 대체)의 수.
   */
  private static double levenshteinDistance(String str1, String str2) {
    int[][] dp = new int[str1.length() + 1][str2.length() + 1];

    for (int i = 0; i <= str1.length(); i++) {
      dp[i][0] = i;
    }

    for (int j = 0; j <= str2.length(); j++) {
      dp[0][j] = j;
    }

    for (int i = 1; i <= str1.length(); i++) {
      for (int j = 1; j <= str2.length(); j++) {
        int cost = (str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1;
        dp[i][j] = Math.min(
            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
            dp[i - 1][j - 1] + cost
        );
      }
    }

    // 최대 가능한 거리 (더 긴 문자열의 길이)
    int maxLength = Math.max(str1.length(), str2.length());

    return 1.0 - ((double) dp[str1.length()][str2.length()] / maxLength);
  }


  // 자카드 유사도 계산
  private static double calculateJaccardSimilarity(String[] words1, String[] words2) {
    // 중복 제거를 위해 Set으로 변환
    Set<String> set1 = new HashSet<>(Arrays.asList(words1));
    Set<String> set2 = new HashSet<>(Arrays.asList(words2));

    // 교집합을 계산
    Set<String> intersection = new HashSet<>(set1);
    intersection.retainAll(set2);

    // 합집합을 계산
    Set<String> union = new HashSet<>(set1);
    union.addAll(set2);

    // 자카드 유사도 = 교집합 크기 / 합집합 크기
    return union.isEmpty() ? 1.0 : (double) intersection.size() / union.size();
  }
}