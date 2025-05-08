package com.sprint.monew.common.config.api;


import com.sprint.monew.common.util.CursorPageResponseDto;
import com.sprint.monew.domain.interest.dto.InterestCreateRequest;
import com.sprint.monew.domain.interest.dto.InterestDto;
import com.sprint.monew.domain.interest.dto.InterestUpdateRequest;
import com.sprint.monew.domain.interest.subscription.SubscriptionDto;
import com.sprint.monew.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "관심사 관리", description = "관심사 관련 API")
public interface InterestApi {

  //관심사 목록 조회
  @Operation(summary = "관심사 목록 조회", description = "조건에 맞는 관심사 목록을 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공",
          content = @Content(schema = @Schema(implementation = InterestDto.class))),
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
  ResponseEntity<CursorPageResponseDto> getInterests(
      @Parameter(description = "사용자 ID") @RequestHeader("Monew-Request-User-ID") UUID userId,
      @Parameter(description = "검색어 (관심사 이름, 키워드)") @RequestParam String keyword,
      @Parameter(description = "정렬 속성 이름",
          schema = @Schema(allowableValues = {"name", "subscriberCount"})
      ) @RequestParam String orderBy,
      @Parameter(description = "정렬 방향 (ASC, DESC)",
          schema = @Schema(allowableValues = {"ASC", "DESC"})
      ) @RequestParam String direction,
      @Parameter(description = "커서 값") UUID cursor,
      @Parameter(description = "보조 커서 값") @RequestParam Instant after,
      @Parameter(description = "커서 페이지 크기", example = "50") @RequestParam Integer limit);

  //관심사 등록
  @Operation(summary = "관심사 등록", description = "새로운 관심사를 등록합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "등록 성공",
          content = @Content(schema = @Schema(implementation = InterestDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청(입력값 검증 실패)",
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
      @ApiResponse(responseCode = "409", description = "유사 관심사 중복",
          content = @Content(mediaType = "*/*",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = {@ExampleObject(
                  value =
                      """ 
                          {
                            "timestamp": "2025-05-07T09:09:00.777004Z",
                            "code": "INTEREST_ALREADY_EXISTS",
                            "message": "유사한 이름의 관심사가 이미 존재합니다.",
                            "details": {},
                            "exceptionType": "InterestAlreadyExistsException",
                            "status": 409
                          }
                          """
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
  ResponseEntity<InterestDto> addInterest(
      @RequestBody InterestCreateRequest interestCreateRequest);

  //관심사 구독
  @Operation(summary = "관심사 구독", description = "관심사를 구독합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "구독 성공",
          content = @Content(schema = @Schema(implementation = SubscriptionDto.class))),
      @ApiResponse(responseCode = "404", description = "관심사 정보 없음",
          content = @Content(mediaType = "*/*",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = {@ExampleObject(
                  value =
                      """
                          {
                            "timestamp": "2025-05-07T09:14:19.925338Z",
                            "code": "INTEREST_NOT_FOUND",
                            "message": "관심사 정보를 찾을 수 없습니다.",
                            "details": {
                              "interestId": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
                            },
                            "exceptionType": "InterestNotFoundException",
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
  ResponseEntity<SubscriptionDto> subscribeInterest(
      @Parameter(description = "관심사 ID") @PathVariable UUID interestId,
      @Parameter(description = "사용자 ID") @RequestHeader("Monew-Request-User-ID") UUID userId);


  //관심사 구독 취소
  @Operation(summary = "관심사 구독 취소", description = "관심사 구독을 취소합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "구독 취소 성공"),
      @ApiResponse(responseCode = "404", description = "관심사 정보 없음"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  ResponseEntity<Void> unsubscribeInterest(
      @Parameter(description = "관심사 ID") @PathVariable UUID interestId,
      @Parameter(description = "사용자 ID") @RequestHeader("Monew-Request-User-ID") UUID userId);

  //관심사 물리 삭제
  @Operation(summary = "관심사 물리 삭제", description = "관심사를 물리적으로 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "삭제 성공"),
      @ApiResponse(responseCode = "404", description = "관심사 정보 없음"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  ResponseEntity<Void> deleteInterest(@PathVariable UUID interestId);

  //관심사 정보 수정
  @Operation(summary = "관심사 정보 수정", description = "관심사의 키워드를 수정합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "수정 성공",
          content = @Content(schema = @Schema(implementation = InterestDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청(입력값 검증 실패)",
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
      @ApiResponse(responseCode = "404", description = "관심사 정보 없음",
          content = @Content(mediaType = "*/*",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = {@ExampleObject(
                  value =
                      """ 
                          {
                            "timestamp": "2025-05-07T09:18:34.774052Z",
                            "code": "INTEREST_NOT_FOUND",
                            "message": "관심사 정보를 찾을 수 없습니다.",
                            "details": {
                              "interestId": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
                            },
                            "exceptionType": "InterestNotFoundException",
                            "status": 404
                          }
                          """
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
  ResponseEntity<InterestDto> updateInterest(
      @Parameter(description = "관심사 ID") @PathVariable UUID interestId,
      @RequestBody InterestUpdateRequest interestUpdateRequest,
      @Parameter(description = "사용자 ID") @RequestHeader(name = "Monew-Request-User-ID", required = false) UUID userId);
}
