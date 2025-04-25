package com.sprint.monew.domain.interest.dto;

import java.util.List;

public record InterestCreateRequest(
    String name,
    List<String> keywords
) {

  // 나중에 컴팩트 생성자로 KEYWORD 대/소문자 어떻게 통일시킬지 결정하기
}
