package com.sprint.monew.common.batch.util;

import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.articleinterest.ArticleInterest;
import java.util.List;

public record ArticlesAndArticleInterestsDTO(
    List<Article> articleList,
    List<ArticleInterest> articleInterests) {
}
