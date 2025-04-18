package com.sprint.monew.domain.interest;

import com.sprint.monew.common.util.CursorPageResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interests")
public class InterestController {

  //관심사 목록 조회
  @GetMapping
  public ResponseEntity<CursorPageResponseDto> getInterests() {
    return null;
  }

  //관심사 등록
  @PostMapping
  public ResponseEntity<InterestDto> addInterest(@RequestBody Interest interest) {
    return null;
  }

  //관심사 구독
  @PostMapping("/{interestId}/subscriptions")
  public ResponseEntity<InterestDto> subscribeInterest(@PathVariable String interestId) {
    //헤더 - 요청자 id
    return null;
  }

  //관심사 구독 취소
  @DeleteMapping("/{interestId}/subscriptions")
  public ResponseEntity<?> unsubscribeInterest(@PathVariable String interestId) {
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
