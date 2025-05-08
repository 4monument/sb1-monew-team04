package com.sprint.monew.domain.interest.dto;

import jakarta.validation.constraints.Size;
import java.util.List;

public record InterestUpdateRequest(
    @Size(min = 1, max = 10) List<String> keywords
) {

}
