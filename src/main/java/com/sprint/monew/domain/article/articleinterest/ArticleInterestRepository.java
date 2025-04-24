package com.sprint.monew.domain.article.articleinterest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleInterestRepository extends
    JpaRepository<ArticleInterest, ArticleInterestKey> {

}
