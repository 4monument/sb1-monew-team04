package com.sprint.monew.common.util;

public class SimilarityCalculator {

  //두 문자열 간 유사도 검사
  public static double calculateSimilarity(String str1, String str2) {
    if (str1.equals(str2)) {
      return 1.0;
    }

    if (str1 == null || str2 == null || str1.isEmpty() || str2.isEmpty()) {
      return 0.0;
    }

    int distance = levenshteinDistance(str1, str2);

    // 최대 가능한 거리 (더 긴 문자열의 길이)
    int maxLength = Math.max(str1.length(), str2.length());

    //유사도 계산
    return 1.0 - ((double) distance / maxLength);
  }

  /**
   * 레벤슈타인 거리를 계산 한 문자열에서 다른 문자열로 변환하는 데 필요한 최소 편집 연산(삽입, 삭제, 대체)의 수.
   */
  private static int levenshteinDistance(String str1, String str2) {
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
    return dp[str1.length()][str2.length()];
  }
}
