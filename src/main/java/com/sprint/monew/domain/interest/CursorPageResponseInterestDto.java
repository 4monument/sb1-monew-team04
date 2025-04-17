package com.sprint.monew.domain.interest;

import java.time.Instant;
import java.util.List;

public class CursorPageResponseInterestDto {

  private List<InterestDto> content;
  private String nextCursor;
  private Instant nextAfter;
  private int size;
  private int totalElements;
  private boolean hasNext;
}
