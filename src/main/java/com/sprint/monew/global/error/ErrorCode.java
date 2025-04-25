package com.sprint.monew.global.error;

import lombok.Getter;

@Getter
public enum ErrorCode {

  // User 관련 에러 코드
  USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
  DUPLICATE_USER("이미 존재하는 사용자입니다."),
  INVALID_USER_CREDENTIALS("잘못된 사용자 인증 정보입니다."),
  ALREADY_DELETED_USER("이미 삭제된 유저입니다."),

  // Interest 관련 에러코드
  INTEREST_ALREADY_EXISTS("유사한 이름의 관심사가 이미 존재합니다."),
  INTEREST_NOT_FOUND("관심사 정보를 찾을 수 없습니다."),
  EMPTY_KEYWORDS_NOT_ALLOWED("키워드는 한 개 이상 포함되어야 합니다."),

  ARTICLE_NOT_FOUND("기사를 찾을 수 없습니다."),
  ARTICLE_VIEW_ALREADY_EXIST("이미 존재하는 기사 뷰 입니다.");
  private final String message;

  ErrorCode(String message) {
    this.message = message;
  }
}