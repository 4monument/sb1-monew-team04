package com.sprint.monew.domain.comment;

import com.sprint.monew.common.config.api.CommentApi;
import com.sprint.monew.common.util.CursorPageResponseDto;
import com.sprint.monew.domain.comment.dto.CommentDto;
import com.sprint.monew.domain.comment.dto.CommentLikeDto;
import com.sprint.monew.domain.comment.dto.request.CommentRegisterRequest;
import com.sprint.monew.domain.comment.dto.CommentCondition;
import com.sprint.monew.domain.comment.dto.request.CommentUpdateRequest;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController implements CommentApi {

  private final CommentService commentService;

  @GetMapping
  public ResponseEntity<CursorPageResponseDto<CommentDto>> getComments(
      @RequestParam(required = false) UUID articleId,
      @RequestParam(required = false) String cursor,
      @RequestParam(required = false) Instant after,
      @RequestParam String orderBy,
      @RequestParam String direction,
      @RequestParam int limit,
      @RequestHeader("Monew-Request-User-ID") UUID userId
  ) {
    PageRequest pageRequest = PageRequest
        .of(0, limit, Direction.fromString(direction), orderBy);
    CommentCondition commentCondition = new CommentCondition(articleId, cursor, after);
    return ResponseEntity.ok(commentService.getComments(commentCondition, userId, pageRequest));
  }

  @PostMapping
  public ResponseEntity<CommentDto> addComment(
      @RequestBody CommentRegisterRequest commentRegisterRequest
  ) {
    return ResponseEntity
        .status(201)
        .body(commentService.registerComment(commentRegisterRequest));
  }

  @PostMapping("/{commentId}/comment-likes")
  public ResponseEntity<CommentLikeDto> likeComment(
      @PathVariable UUID commentId,
      @RequestHeader("Monew-Request-User-ID") UUID userId
  ) {
    return ResponseEntity.ok(commentService.commentLike(commentId, userId));
  }

  @DeleteMapping("/{commentId}/comment-likes")
  public ResponseEntity<Void> unlikeComment(
      @PathVariable UUID commentId,
      @RequestHeader("Monew-Request-User-ID") UUID userId
  ) {
    commentService.unlikeComment(commentId, userId);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{commentId}")
  public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId) {
    commentService.deleteComment(commentId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{commentId}")
  public ResponseEntity<CommentDto> updateComment(
      @PathVariable UUID commentId,
      @RequestBody CommentUpdateRequest commentUpdateRequest,
      @RequestHeader("Monew-Request-User-ID") UUID userId
  ) {
    CommentDto commentDto = commentService
        .updateCommentContent(commentId, userId, commentUpdateRequest);
    return ResponseEntity.ok(commentDto);
  }

  @DeleteMapping("/{commentId}/hard")
  public ResponseEntity<Void> hardDeleteComment(@PathVariable UUID commentId) {
    commentService.hardDeleteComment(commentId);
    return ResponseEntity.noContent().build();
  }
}
