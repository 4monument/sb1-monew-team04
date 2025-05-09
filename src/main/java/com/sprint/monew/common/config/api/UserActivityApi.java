package com.sprint.monew.common.config.api;

import com.sprint.monew.domain.activity.UserActivityDto;
import com.sprint.monew.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "사용자 활동 내역 관리", description = "사용자 활동 내역 관련 API")
public interface UserActivityApi {


  @Operation(summary = "사용자 활동 내역 조회", description = "사용자 ID로 활동 내역을 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "사용자 활동 내역 조회 성공",
          content = @Content(schema = @Schema(implementation = UserActivityDto.class))),
      @ApiResponse(responseCode = "404", description = "사용자 정보 없음",
          content = @Content(mediaType = "*/*",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = {@ExampleObject(
                  value = """
                      {
                        "timestamp": "2025-05-07T02:20:40.204183Z",
                        "code": "ACTIVITY_NOT_FOUND",
                        "message": "활동내역을 찾을 수 없습니다.",
                        "details": {
                          "UUID": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaad"
                        },
                        "exceptionType": "UserActivityNotFoundException",
                        "status": 404
                      }"""
              )}
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
  ResponseEntity<UserActivityDto> getUserActivityFromMongo(
      @Parameter(description = "사용자 ID")
      @PathVariable UUID userId,
      @RequestHeader(name = "Monew-Request-User-ID", required = false) UUID headerUserId
  );


  @Operation(summary = "사용자 활동 내역 조회", description = "사용자 ID로 활동 내역을 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "사요자 활동 내역 조회 성공",
          content = @Content(schema = @Schema(implementation = UserActivityDto.class))),
      @ApiResponse(responseCode = "404", description = "사용자 정보 없음",
          content = @Content(mediaType = "*/*",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = {@ExampleObject(
                  value = """
                      {\s
                        "timestamp": "2025-05-07T02:20:40.204183Z",\s
                        "code": "ACTIVITY_NOT_FOUND",\s
                        "message": "활동내역을 찾을 수 없습니다.",\s
                        "details": {\s
                          "UUID": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaad"\s
                        },\s
                        "exceptionType": "UserActivityNotFoundException",\s
                        "status": 404\s
                      }"""
              )}
          )
      )})
  ResponseEntity<UserActivityDto> getUserActivity(
      @Parameter(description = "사용자 ID")
      @PathVariable UUID userId,
      @RequestHeader(name = "Monew-Request-User-ID", required = false) UUID headerUserId
  );

  ResponseEntity<UserActivityDto> saveUserActivityToMongo(
      @Parameter(description = "사용자 ID")
      @PathVariable UUID userId,
      @RequestHeader(name = "Monew-Request-User-ID", required = false) UUID headerUserId
  );

  ResponseEntity<Void> updateUserActivityToMongo(
      @Parameter(description = "사용자 ID")
      @PathVariable UUID userId,
      @RequestHeader(name = "Monew-Request-User-ID", required = false) UUID headerUserId
  );
}
