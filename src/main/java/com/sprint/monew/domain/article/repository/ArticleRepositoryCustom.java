package com.sprint.monew.domain.article.repository;

import com.sprint.monew.domain.article.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleRepositoryCustom {

  Page<Article> findArticles(Pageable pageable);
}
