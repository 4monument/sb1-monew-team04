package com.sprint.monew.domain.comment.repository;

import com.sprint.monew.domain.comment.dto.CommentDto;
import com.sprint.monew.domain.comment.dto.request.CommentRequest;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {

  Page<CommentDto> getComments(CommentRequest condition, UUID userId, Pageable pageable);

}
