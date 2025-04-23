package com.sprint.monew.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
  USER_NOT_FOUND(404, "User Not Found"),
  ARTICLE_NOT_FOUND(404, "Article Not Found"),
  ARTICLE_VIEW_ALREADY_EXIST(400, "ArticleView already exist"),

  VALIDATION_ERROR(400, "Validation Error");

  private final int status;
  private final String message;
}