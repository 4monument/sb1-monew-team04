package com.sprint.monew.domain.comment.repository;

import com.sprint.monew.domain.comment.dto.CommentDto;
import com.sprint.monew.domain.comment.dto.request.CommentRequest;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CommentRepositoryCustom {

  Slice<CommentDto> getComments(CommentRequest condition, UUID userId, Pageable pageable);

}
