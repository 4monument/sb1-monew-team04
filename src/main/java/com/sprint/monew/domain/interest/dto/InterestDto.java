package com.sprint.monew.domain.interest.dto;

import com.sprint.monew.domain.interest.Interest;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

public record InterestDto(
    @Schema(description = "관심사 ID")
    UUID id,
    @Schema(description = "관심사 이름")
    String name,
    @Schema(description = "관련 키워드 목록")
    List<String> keywords,
    @Schema(description = "구독자 수")
    long subscriberCount,
    @Schema(description = "요청자의 구독 여부")
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
}
