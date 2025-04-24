package com.sprint.monew.common.batch;

import com.sprint.monew.common.batch.util.Keywords;
import com.sprint.monew.domain.article.Article;
import com.sprint.monew.domain.article.api.ArticleApiDto;
import java.util.List;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NaverArticleCollectProcessor implements ItemProcessor<Object, Article> {

  private final List<ArticleApiDto> articleApiDtos;
  private final Keywords keywords;

  public NaverArticleCollectProcessor(
      @Value("#{JobExecutionContext['articleApiDtos']}") List<ArticleApiDto> articleApiDtos,
      @Value("#{JobExecutionContext['keywords']}") Keywords keywords) {
    this.articleApiDtos = articleApiDtos;
    this.keywords = keywords;
  }

  @Override
  public Article process(Object item) throws Exception {
    if (keywords == null){
      // 변환 로직 넣기
      return null;
    }

    List<ArticleApiDto> filteredArticleDtos = keywords.filter(articleApiDtos);

    // 변환로직

    return null;
  }
}
