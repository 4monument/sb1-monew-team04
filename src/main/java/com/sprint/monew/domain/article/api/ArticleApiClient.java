package com.sprint.monew.domain.article.api;

import com.sprint.monew.domain.article.Article.Source;
import com.sprint.monew.domain.article.api.chosun.ChosunArticleClient;
import com.sprint.monew.domain.article.api.chosun.ChosunArticleClient.ChosunCategory;
import com.sprint.monew.domain.article.api.chosun.ChosunArticleResponse;
import com.sprint.monew.domain.article.api.hankyung.HankyungArticleClient;
import com.sprint.monew.domain.article.api.hankyung.HankyungArticleClient.HankyungCategory;
import com.sprint.monew.domain.article.api.hankyung.HankyungArticleResponse;
import com.sprint.monew.domain.article.api.naver.NaverArticleClient;
import com.sprint.monew.domain.article.api.naver.NaverArticleResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleApiClient {

  private final NaverArticleClient naverArticleClient;
  private final ChosunArticleClient chosunArticleClient;
  private final HankyungArticleClient hankyungArticleClient;

  /**
   * @param query 검색어. query가 포함된 기사를 수집함. 필수값
   * @param display 한 번에 표시할 검색 결과 개수(기본값:10, 최댓값: 100)
   * @param start 검색 시작 위치(기본값: 1, 최댓값: 1000)
   */
  public List<ArticleApiDto> getNaverArticle(String query, int display, int start) {
    if (display < 10 || display > 100) {
      display = 10;
    }
    if (start < 1 || start > 1000) {
      start = 1;
    }
    NaverArticleResponse article = naverArticleClient.getArticle(query, display, start);
    return article.items().stream()
        .map(item ->
          ArticleApiDto.builder()
              .source(Source.NAVER)
              .sourceUrl(item.originallink())
              .title(item.title())
              .summary(item.description())
              .publishDate(item.pubDate().toInstant())
              .build()
        )
        .toList();
  }

  /**
   * @param query query만 넣고 싶으면 이거 쓰세요
   */
  public List<ArticleApiDto> getNaverArticle(String query) {
    return getNaverArticle(query, 10, 1);
  }

  /**
   * @param category 카테고리 별로 가져올 수 있어요.
   *                 ALL로 하시면 카테고리 구별 없이 가져올 수 있어요.
   */
  public List<ArticleApiDto> getChosunArticle(ChosunCategory category) {
    ChosunArticleResponse article;
    if (category == ChosunCategory.ALL) {
      article = chosunArticleClient.getArticle();
    } else {
      article = chosunArticleClient.getArticle(category);
    }

    return article.channel().items().stream()
        .map(item ->
            ArticleApiDto.builder()
                .source(Source.CHOSUN)
                .sourceUrl(item.link())
                .title(item.title())
                .summary(item.description())
                .publishDate(item.pubDate().toInstant())
                .build()
        )
        .toList();
  }

  /**
   * @param category 카테고리 별로 가져올 수 있어요.
   *                 ALL로 하시면 카테고리 구별 없이 가져올 수 있어요.
   */
  public List<ArticleApiDto> getHankyungArticle(HankyungCategory category) {
    HankyungArticleResponse article = hankyungArticleClient.getArticle(category);
    return article.channel().items().stream()
        .map(item ->
            ArticleApiDto.builder()
                .source(Source.HANKYUNG)
                .sourceUrl(item.link())
                .title(item.title())
                .summary(item.title())
                .publishDate(item.pubDate().toInstant())
                .build()
        )
        .toList();
  }
}
