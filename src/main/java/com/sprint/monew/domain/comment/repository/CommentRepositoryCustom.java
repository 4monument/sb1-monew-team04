package com.sprint.monew.domain.comment.repository;

import com.sprint.monew.domain.comment.dto.CommentDto;
import com.sprint.monew.domain.comment.dto.CommentCondition;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CommentRepositoryCustom {

  Slice<CommentDto> getComments(CommentCondition condition, UUID userId, Pageable pageable);

}
