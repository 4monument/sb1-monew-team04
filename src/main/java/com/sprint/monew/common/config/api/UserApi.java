package com.sprint.monew.common.config.api;

import com.sprint.monew.domain.user.UserDto;
import com.sprint.monew.domain.user.UserLoginRequest;
import com.sprint.monew.domain.user.UserRegisterRequest;
import com.sprint.monew.domain.user.UserUpdateRequest;
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
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "사용자 관리", description = "사용자 관련 API")
public interface UserApi {

  @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "회원가입 성공",
          content = @Content(schema = @Schema(implementation = UserDto.class))),
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
                              "email": "이메일 형식으로 입력해주세요"
                            },
                            "exceptionType": "DomainException",
                            "status": 400
                          }"""
              )}
          )
      ),
      @ApiResponse(responseCode = "409", description = "이메일 중복",
          content = @Content(mediaType = "*/*",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = {@ExampleObject(
                  value =
                      """ 
                          {
                            "timestamp": "2025-05-07T02:58:46.521263Z",
                            "code": "DUPLICATE_USER",
                            "message": "이미 존재하는 사용자입니다.",
                            "details": {
                              "email": "test@monew.com"
                            },
                            "exceptionType": "EmailAlreadyExistsException",
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
  ResponseEntity<UserDto> register(@RequestBody UserRegisterRequest request);


  @Operation(summary = "로그인", description = "사용자 로그인을 처리합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "로그인 성공",
          content = @Content(schema = @Schema(implementation = UserDto.class))),
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
                              "email": "이메일 형식으로 입력해주세요"
                            },
                            "exceptionType": "DomainException",
                            "status": 400
                          }"""
              )}
          )
      ),
      @ApiResponse(responseCode = "401", description = "로그인 실패(이메일 또는 비밀번호 불일치)",
          content = @Content(mediaType = "*/*",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = {@ExampleObject(
                  value =
                      """ 
                          {
                              "timestamp": "2025-05-07T03:01:27.849274Z",
                              "code": "INVALID_USER_CREDENTIALS",
                              "message": "잘못된 사용자 인증 정보입니다.",
                              "details": {},
                              "exceptionType": "InvalidCredentialsException",
                              "status": 401
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
  ResponseEntity<UserDto> login(@RequestBody UserLoginRequest request);


  @Operation(summary = "사용자 정보 수정", description = "사용자의 닉네임을 수정합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "사용자 정보 수정 성공",
          content = @Content(schema = @Schema(implementation = UserDto.class))),
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
                              "nickname": "최소 한글자 이상이어야합니다."
                            },
                            "exceptionType": "DomainException",
                            "status": 400
                          }"""
              )}
          )
      ),
      @ApiResponse(responseCode = "409", description = "이메일 중복",
          content = @Content(mediaType = "*/*",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = {@ExampleObject(
                  value =
                      """ 
                          {
                            "timestamp": "2025-05-07T02:58:46.521263Z",
                            "code": "DUPLICATE_USER",
                            "message": "이미 존재하는 사용자입니다.",
                            "details": {
                              "email": "test@monew.com"
                            },
                            "exceptionType": "EmailAlreadyExistsException",
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
  ResponseEntity<UserDto> updateNickname(
      @Parameter(description = "사용자 ID")
      @PathVariable UUID userId,
      @RequestBody UserUpdateRequest request);


  @Operation(summary = "사용자 논리 삭제", description = "사용자를 논리적으로 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "사용자 삭제 성공"),
      @ApiResponse(responseCode = "403", description = "사용자 삭제 권한 없음"),
      @ApiResponse(responseCode = "404", description = "사용자 정보 없음"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  ResponseEntity<Void> softDelete(
      @Parameter(description = "사용자 ID")
      @PathVariable UUID userId);

  @Operation(summary = "사용자 물리 삭제", description = "사용자를 물리적으로 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "사용자 삭제 성공"),
      @ApiResponse(responseCode = "403", description = "사용자 삭제 권한 없음"),
      @ApiResponse(responseCode = "404", description = "사용자 정보 없음"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  ResponseEntity<Void> hardDelete(
      @Parameter(description = "사용자 ID")
      @PathVariable UUID userId);
}