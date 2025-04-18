package com.sprint.monew.domain.interest;

import java.util.List;

public record InterestCreateRequest(
    String name,
    List<String> keywords
) {
}
