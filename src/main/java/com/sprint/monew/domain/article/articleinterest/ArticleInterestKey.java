package com.sprint.monew.domain.article.articleinterest;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class ArticleInterestKey implements Serializable {
  private UUID articleId;
  private UUID interestId;
}
