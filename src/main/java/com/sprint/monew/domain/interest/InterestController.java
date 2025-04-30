package com.sprint.monew.domain.interest;

import com.sprint.monew.common.util.CursorPageResponseDto;
import com.sprint.monew.domain.interest.dto.InterestCreateRequest;
import com.sprint.monew.domain.interest.dto.InterestDto;
import com.sprint.monew.domain.interest.dto.InterestSearchRequest;
import com.sprint.monew.domain.interest.dto.InterestUpdateRequest;
import com.sprint.monew.domain.interest.subscription.SubscriptionDto;
import java.util.UUID;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
public class InterestController {

  private final InterestService interestService;

  //관심사 목록 조회
  @GetMapping
  public ResponseEntity<CursorPageResponseDto> getInterests(
      @RequestHeader("Monew-Request-User-ID") UUID userId, InterestSearchRequest request) {
    return ResponseEntity.ok(
        interestService.getInterestsWithSubscriberInfo(request, userId));
  }

  //관심사 등록
  @PostMapping
  public ResponseEntity<InterestDto> addInterest(
      @RequestBody InterestCreateRequest interestCreateRequest) {
    InterestDto interestDto = interestService.createInterest(interestCreateRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(interestDto);
  }

  //관심사 구독
  @PostMapping("/{interestId}/subscriptions")
  public ResponseEntity<SubscriptionDto> subscribeInterest(@PathVariable UUID interestId,
      @RequestHeader("Monew-Request-User-ID") UUID userId) {
    return ResponseEntity.ok(
        interestService.subscribeToInterest(interestId, userId));
  }

  //관심사 구독 취소
  @DeleteMapping("/{interestId}/subscriptions")
  public ResponseEntity<Void> unsubscribeInterest(@PathVariable UUID interestId,
      @RequestHeader("Monew-Request-User-ID") UUID userId) {
    interestService.unsubscribeFromInterest(interestId, userId);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  //관심사 물리 삭제
  @DeleteMapping("/{interestId}")
  public ResponseEntity<Void> deleteInterest(@PathVariable UUID interestId) {
    interestService.deleteInterest(interestId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  //관심사 정보 수정
  @PatchMapping("/{interestId}")
  public ResponseEntity<InterestDto> updateInterest(@PathVariable UUID interestId,
      @RequestBody InterestUpdateRequest interestUpdateRequest,
      @RequestHeader(name = "Monew-Request-User-ID", required = false) UUID userId) {
    return ResponseEntity.ok(
        interestService.updateInterest(userId, interestId, interestUpdateRequest));
  }
}
