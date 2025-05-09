package com.sprint.monew.domain.interest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record InterestCreateRequest(
    @Schema(description = "관심사 이름")
    @Size(min = 1, max = 50)
    String name,

    @Schema(description = "관련 키워드 목록")
    @Size(min = 1, max = 10)
    List<@NotBlank @Size(min = 1, max = 20) String> keywords
) {

}
