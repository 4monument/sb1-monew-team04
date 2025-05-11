package com.sprint.monew.common.config.api;

import com.sprint.monew.common.util.CursorPageResponseDto;
import com.sprint.monew.domain.notification.dto.NotificationDto;
import com.sprint.monew.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "알림 관리", description = "알림 관련 API")
public interface NotificationApi {

  // 알림 목록 조회
  @Operation(summary = "알림 목록 조회", description = "알림 목록을 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공",
          content = @Content(schema = @Schema(implementation = NotificationDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (정렬 기준 오류, 페이지네이션 파라미터 오류 등)",
          content = @Content(mediaType = "*/*",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = {@ExampleObject(
                  value =
                      """
                          {
                            "timestamp": "2025-05-07T02:33:49.416621602Z",
                            "code": "INVALID_INPUT_VALUE",
                            "message": "잘못된 입력값입니다.",
                            "details": {
                          
                            },
                            "exceptionType": "DomainException",
                            "status": 400
                          }"""
              )} // todo - 입력값 유효성 검사
          )
      ),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류",
          content = @Content(mediaType = "*/*",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = {@ExampleObject(
                  value = """
                      {
                        "timestamp": "2025-05-07T02:20:40.204183Z",
                        "code": "INTERNAL_SERVER_ERROR",
                        "message": "예상치 못한 오류 발생",
                        "details": {
                        },
                        "exceptionType": "INTERNAL_SERVER_ERROR",
                        "status": 500
                      }"""
              )}
          )
      )})
  ResponseEntity<CursorPageResponseDto> getNotifications(
      @Parameter(description = "커서 값") Instant cursor,
      @Parameter(description = "보조 커서(createdAt) 값") @RequestParam Instant after,
      @Parameter(description = "커서 페이지 크기", example = "50") @RequestParam @Min(1) @Max(100) Integer limit,
      @Parameter(description = "요청자 ID") @RequestHeader("Monew-Request-User-ID") UUID userId);

  // 전체 알림 확인
  @Operation(summary = "전체 알림 확인", description = "전체 알림을 한번에 확인합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "전체 알림 확인 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청(입력값 검증 실패)"),
      @ApiResponse(responseCode = "404", description = "사용자 정보 없음"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  ResponseEntity<Void> checkAllNotifications(
      @Parameter(description = "요청자 ID") @RequestHeader("Monew-Request-User-ID") UUID userId);

  // 알림 확인(단일)
  @Operation(summary = "알림 확인", description = "알림을 확인합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "알림 확인 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청(입력값 검증 실패)"),
      @ApiResponse(responseCode = "404", description = "사용자 정보 없음"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  ResponseEntity<Void> checkNotification(
      @Parameter(description = "알림 ID") @PathVariable UUID notificationId,
      @Parameter(description = "요청자 ID") @RequestHeader("Monew-Request-User-ID") UUID userId);

}
