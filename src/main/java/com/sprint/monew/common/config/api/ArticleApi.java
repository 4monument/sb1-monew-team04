package com.sprint.monew.common.config.api;

import com.sprint.monew.common.util.CursorPageResponseDto;
import com.sprint.monew.domain.article.dto.ArticleDto;
import com.sprint.monew.domain.article.dto.ArticleRestoreResultDto;
import com.sprint.monew.domain.article.dto.ArticleSortDirection;
import com.sprint.monew.domain.article.dto.ArticleViewDto;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "뉴스 기사 관리", description = "뉴스 기사 관련 API")
public interface ArticleApi {

  @Operation(summary = "기사 뷰 등록", description = "기사 뷰를 등록합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "기사 뷰 등록 성공",
          content = @Content(schema = @Schema(implementation = ArticleViewDto.class))),
      @ApiResponse(responseCode = "404", description = "댓글 정보 없음",
          content = @Content(mediaType = "*/*",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = {@ExampleObject(
                  value =
                      """ 
                          {
                            "timestamp": "2025-05-07T09:18:34.774052Z",
                            "code": "INTEREST_NOT_FOUND",
                            "message": "댓글 정보를 찾을 수 없습니다.",
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
  ResponseEntity<ArticleViewDto> registerArticleView(
      @Parameter(description = "기사 ID") @PathVariable UUID articleId,
      @Parameter(description = "요청자 ID") @RequestHeader("Monew-Request-User-ID") UUID userId
  );

  @Operation(summary = "뉴스 기사 목록 조회", description = "조건에 맞는 뉴스 기사 목록을 조회합니다.")
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
  ResponseEntity<CursorPageResponseDto<ArticleDto>> getArticles(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) UUID interestId,
      @RequestParam(required = false) List<String> sourceIn,
      @RequestParam(required = false) LocalDateTime publishDateFrom,
      @RequestParam(required = false) LocalDateTime publishDateTo,
      @RequestParam(required = false) String cursor,
      @RequestParam(required = false) Instant after,
      @RequestParam String orderBy,
      @RequestParam ArticleSortDirection direction,
      @RequestParam int limit,
      @RequestHeader("Monew-Request-User-ID") UUID userId
  );

  @Operation(summary = "뉴스 기사 복구", description = "유실된 뉴스 기사를 복구합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "복구 성공",
          content = @Content(schema = @Schema(implementation = ArticleRestoreResultDto.class))),
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
  ResponseEntity<List<ArticleRestoreResultDto>> restoreArticles(
      @Parameter(description = "날짜 시작(범위)") @RequestParam Instant from,
      @Parameter(description = "날짜 끝(범위)") @RequestParam Instant to
  )
      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException;

  @Operation(summary = "뉴스 기사 논리 삭제", description = "뉴스 기사를 논리적으로 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "논리 삭제 성공"),
      @ApiResponse(responseCode = "404", description = "뉴스 기사 정보 없음"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  ResponseEntity<Void> deleteArticle(
      @Parameter(description = "기사 ID") @PathVariable UUID id);


  @Operation(summary = "뉴스 기사 물리 삭제", description = "뉴스 기사를 물리적으로 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "삭제 성공"),
      @ApiResponse(responseCode = "404", description = "뉴스 기사 정보 없음"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  ResponseEntity<Void> hardDeleteArticle(
      @Parameter(description = "기사 ID") @PathVariable UUID id);

}
