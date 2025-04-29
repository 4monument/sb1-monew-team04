package com.sprint.monew.domain.interest.dto;

import com.sprint.monew.domain.interest.Interest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class InterestSubscriptionInfoDto {

  private Interest interest;
  private long subscriberCount;

}
