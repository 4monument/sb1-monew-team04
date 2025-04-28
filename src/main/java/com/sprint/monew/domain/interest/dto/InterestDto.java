package com.sprint.monew.domain.interest.dto;

import com.sprint.monew.domain.interest.Interest;
import com.sprint.monew.domain.interest.InterestWithSubscriberCount;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record InterestDto(
    UUID id,
    String name,
    List<String> keywords,
    long subscriberCount,
    boolean subscribedByMe
) {

  public static InterestDto from(Interest interest, long subscriberCount, boolean subscribedByMe) {
    return new InterestDto(
        interest.getId(),
        interest.getName(),
        interest.getKeywords(),
        subscriberCount,
        subscribedByMe
    );
  }

  public static InterestDto from(InterestSubscriptionInfoDto interestSubscriptionInfoDto,
      boolean subscribedByMe) {
    return from(interestSubscriptionInfoDto.getInterest(),
        interestSubscriptionInfoDto.getSubscriberCount(),
        subscribedByMe
    );
  }

  //임시 파싱 메서드
  //todo - queryDsl 적용 시 삭제 또는 리팩토링
  public static InterestDto from(InterestWithSubscriberCount interestWithSubscriberCount,
      boolean subscribedByMe) {

    // keywords 문자열 파싱한 결과를 담을 배열
    List<String> parsedKeywords = new ArrayList<>();
    String rawKeywords = interestWithSubscriberCount.getKeywords();

    // 맨 앞의 '[' 와 맨 뒤의 ']' 제거
    if (rawKeywords != null && !rawKeywords.isEmpty()) {
      String trimmed = rawKeywords.trim();
      if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
        trimmed = trimmed.substring(1, trimmed.length() - 1);
      }

      // 쉼표로 키워드들 분리
      String[] keywordArray = trimmed.split(",");

      for (String keyword : keywordArray) {
        // 각 키워드 정리, 따옴표 및 공백 제거
        keyword = keyword.trim();
        if (keyword.startsWith("\"") && keyword.endsWith("\"")) {
          keyword = keyword.substring(1, keyword.length() - 1);
        }
        // 특수문자 처리된 따옴표 처리 (\" -> ")
        keyword = keyword.replace("\\\"", "\"");

        //처리한 문자열이 비어있지 않다면 키워드!
        if (!keyword.isEmpty()) {
          parsedKeywords.add(keyword);
        }
      }
    }

    return new InterestDto(
        interestWithSubscriberCount.getId(),
        interestWithSubscriberCount.getName(),
        parsedKeywords,
        interestWithSubscriberCount.getSubscriberCount(),
        subscribedByMe
    );
  }
}
