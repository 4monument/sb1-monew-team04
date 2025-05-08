package com.sprint.monew.domain.article.repository;

import com.sprint.monew.domain.article.dto.ArticleDto;
import com.sprint.monew.domain.article.dto.request.ArticleRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ArticleRepositoryCustom {

  Slice<ArticleDto> getArticles(ArticleRequest condition, UUID userId, Pageable pageable);

  List<String> findAllSourceUrl();

  Long getArticleCount(ArticleRequest condition);
}