package com.sprint.monew.domain.interest;

import com.sprint.monew.common.util.CursorPageResponseDto;
import com.sprint.monew.domain.interest.dto.InterestCreateRequest;
import com.sprint.monew.domain.interest.dto.InterestDto;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
public class InterestController {

  private final InterestService interestService;

  //관심사 목록 조회
  @GetMapping
  public ResponseEntity<CursorPageResponseDto> getInterests(
      @RequestHeader("Monew-Request-User-ID") String userId,
      @RequestParam String keyword,
      @RequestParam(required = false) String orderBy,
      @RequestParam(required = false) String direction,
      @RequestParam(required = false) String cursor,
      @RequestParam Instant after,
      @RequestParam int limit
  ) {
    return null;
  }

  //관심사 등록
  @PostMapping
  public ResponseEntity<InterestDto> addInterest(@RequestBody InterestCreateRequest interestCreateRequest) {
    InterestDto interestDto = interestService.createInterest(interestCreateRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(interestDto);
  }

  //관심사 구독
  @PostMapping("/{interestId}/subscriptions")
  public ResponseEntity<InterestDto> subscribeInterest(@PathVariable String interestId,
      @RequestHeader("Monew-Request-User-ID") String userId) {
    //헤더 - 요청자 id
    return null;
  }

  //관심사 구독 취소
  @DeleteMapping("/{interestId}/subscriptions")
  public ResponseEntity<?> unsubscribeInterest(@PathVariable String interestId,
      @RequestHeader("Monew-Request-User-ID") String userId) {
    return null;
  }

  //관심사 물리 삭제
  @DeleteMapping("/{interestId}")
  public ResponseEntity<?> deleteInterest(@PathVariable String interestId) {
    return null;
  }

  //관심사 물리 삭제
  @PatchMapping("/{interestId}")
  public ResponseEntity<InterestDto> updateInterest(@PathVariable String interestId) {
    return null;
  }

}
