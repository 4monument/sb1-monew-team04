package com.sprint.monew.domain.article.api;

import com.sprint.monew.common.batch.util.Keywords;
import com.sprint.monew.domain.article.Article.Source;
import java.time.Instant;
import lombok.Builder;

@Builder
public record ArticleApiDto (
    Source source,
    String sourceUrl,
    String title,
    String summary,
    Instant publishDate
) {

//  public boolean isRelatedNews(Keywords keywords) {
//    String lowerCaseSummary = summary.toLowerCase();
//    return keywords.getKeywords().stream()
//        .anyMatch(lowerCaseSummary::contains);
//  }
}
