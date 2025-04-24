package com.sprint.monew.domain.article.articleinterest;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class ArticleInterestKey implements Serializable {

  private UUID articleId;
  private UUID interestId;
}
