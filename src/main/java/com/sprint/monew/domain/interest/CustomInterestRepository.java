package com.sprint.monew.domain.interest;

import com.sprint.monew.domain.interest.dto.InterestSubscriptionInfoDto;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface CustomInterestRepository {

  List<InterestSubscriptionInfoDto> getByNameOrKeywordsContaining(String keyword,
      UUID cursorId, Instant afterAt, String sortDirection, String sortField, Pageable pageable);
}
