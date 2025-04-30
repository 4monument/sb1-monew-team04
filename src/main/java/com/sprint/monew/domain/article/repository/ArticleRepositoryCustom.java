package com.sprint.monew.domain.article.repository;

import com.sprint.monew.domain.article.Article;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleRepositoryCustom {

  Page<Article> findArticles(Pageable pageable);

  List<String> findAllSourceUrl();
}