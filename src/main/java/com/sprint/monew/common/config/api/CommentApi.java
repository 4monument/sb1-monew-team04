package com.sprint.monew.common.config.api;


import com.sprint.monew.common.util.CursorPageResponseDto;
import com.sprint.monew.domain.comment.dto.CommentDto;
import com.sprint.monew.domain.comment.dto.CommentLikeDto;
import com.sprint.monew.domain.comment.dto.request.CommentRegisterRequest;
import com.sprint.monew.domain.comment.dto.request.CommentUpdateRequest;
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

@Tag(name = "댓글 관리", description = "댓글 관련 API")
public interface CommentApi {

  @Operation(summary = "댓글 목록 조회", description = "조건에 맞는 댓글 목록을 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공",
          content = @Content(schema = @Schema(implementation = CursorPageResponseDto.class))),
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
  ResponseEntity<CursorPageResponseDto<CommentDto>> getComments(
      @Parameter(description = "기사 ID") @RequestParam(required = false) UUID articleId,
      @Parameter(description = "커서 값")
      @RequestParam(required = false) String cursor,
      @Parameter(description = "보조 커서 값")
      @RequestParam(required = false) Instant after,
      @Parameter(description = "정렬 속성 이름",
          schema = @Schema(allowableValues = {"createdAt", "likeCount"}))
      @RequestParam String orderBy,
      @Parameter(description = "정렬 방향 (ASC, DESC)",
          schema = @Schema(allowableValues = {"ASC", "DESC"}))
      @RequestParam String direction,
      @Parameter(description = "커서 페이지 크기", example = "50") int limit,
      @Parameter(description = "사용자 ID") @RequestHeader("Monew-Request-User-ID") UUID userId
  );


  @Operation(summary = "댓글 등록", description = "새로운 댓글을 등록합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "등록 성공",
          content = @Content(schema = @Schema(implementation = CommentDto.class))),
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
  ResponseEntity<CommentDto> addComment(
      @RequestBody CommentRegisterRequest commentRegisterRequest
  );


  @Operation(summary = "댓글 좋아요", description = "댓글 좋아요를 등록합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "댓글 좋아요 등록 성공",
          content = @Content(schema = @Schema(implementation = CommentDto.class))),
      @ApiResponse(responseCode = "404", description = "댓글 정보 없음",
          content = @Content(mediaType = "*/*",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = {@ExampleObject(
                  value =
                      """
                          {
                            "timestamp": "2025-05-07T09:56:49.125528Z",
                            "code": "COMMENT_NOT_FOUND",
                            "message": "댓글을 찾을 수 없습니다.",
                            "details": {
                              "commentId": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
                            },
                            "exceptionType": "CommentNotFoundException",
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
  ResponseEntity<CommentLikeDto> likeComment(
      @Parameter(description = "댓글 ID") @PathVariable UUID commentId,
      @Parameter(description = "요청자 ID") @RequestHeader("Monew-Request-User-ID") UUID userId
  );

  @Operation(summary = "댓글 좋아요 취소", description = "댓글을 좋아요를 취소합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "댓글 좋아요 취소 성공"),
      @ApiResponse(responseCode = "404", description = "댓글 정보 없음"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  ResponseEntity<Void> unlikeComment(
      @Parameter(description = "댓글 ID") @PathVariable UUID commentId,
      @Parameter(description = "요청자 ID") @RequestHeader("Monew-Request-User-ID") UUID userId
  );


  @Operation(summary = "댓글 논리 삭제", description = "댓글을 논리적으로 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "삭제 성공"),
      @ApiResponse(responseCode = "404", description = "댓글 정보 없음"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  ResponseEntity<Void> deleteComment(
      @Parameter(description = "댓글 ID") @PathVariable UUID commentId);

  @Operation(summary = "댓글 정보 수정", description = "댓글의 내용을 수정합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "수정 성공",
          content = @Content(schema = @Schema(implementation = CommentDto.class))),
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
              )}
          )
      ),
      @ApiResponse(responseCode = "404", description = "댓글 정보 없음",
          content = @Content(mediaType = "*/*",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = {@ExampleObject(
                  value =
                      """
                          {
                            "timestamp": "2025-05-07T09:56:49.125528Z",
                            "code": "COMMENT_NOT_FOUND",
                            "message": "댓글을 찾을 수 없습니다.",
                            "details": {
                              "commentId": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
                            },
                            "exceptionType": "CommentNotFoundException",
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
  ResponseEntity<CommentDto> updateComment(
      @Parameter(description = "댓글 ID") @PathVariable UUID commentId,
      @RequestBody CommentUpdateRequest commentUpdateRequest,
      @Parameter(description = "요청자 ID") @RequestHeader("Monew-Request-User-ID") UUID userId
  );


  @Operation(summary = "댓글 물리 삭제", description = "댓글을 물리적으로 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "삭제 성공"),
      @ApiResponse(responseCode = "404", description = "댓글 정보 없음"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  ResponseEntity<Void> hardDeleteComment(
      @Parameter(description = "댓글 ID") @PathVariable UUID commentId);
}

