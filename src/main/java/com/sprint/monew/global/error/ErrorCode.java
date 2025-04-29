package com.sprint.monew.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

  // User 관련 에러 코드
  USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  DUPLICATE_USER("이미 존재하는 사용자입니다.", HttpStatus.CONFLICT),
  INVALID_USER_CREDENTIALS("잘못된 사용자 인증 정보입니다.", HttpStatus.UNAUTHORIZED),
  ALREADY_DELETED_USER("이미 삭제된 유저입니다.", HttpStatus.BAD_REQUEST),

  // Interest 관련 에러코드
  INTEREST_ALREADY_EXISTS("유사한 이름의 관심사가 이미 존재합니다.", HttpStatus.CONFLICT),
  INTEREST_NOT_FOUND("관심사 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  EMPTY_KEYWORDS_NOT_ALLOWED("키워드는 한 개 이상 포함되어야 합니다.", HttpStatus.BAD_REQUEST),

  // Article 관련 에러코드
  ARTICLE_NOT_FOUND("기사를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  ARTICLE_VIEW_ALREADY_EXIST("이미 존재하는 기사 뷰 입니다.", HttpStatus.CONFLICT),

  // Comment 관련 에러코드
  COMMENT_NOT_FOUND("댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  COMMENT_NOT_OWNED("사용자가 작성하지 않은 댓글입니다.", HttpStatus.BAD_REQUEST),

  LIKE_ALREADY_EXIST("이미 좋아요를 누른 댓글입니다.", HttpStatus.BAD_REQUEST),

  // Activity 관련 에러코드
  ACTIVITY_NOT_FOUND("활동내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

  private final String message;
  private final HttpStatus status;

  ErrorCode(String message, HttpStatus status) {
    this.message = message;
    this.status = status;
  }
}