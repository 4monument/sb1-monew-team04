//package com.sprint.monew.domain.notification;
//
//import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;
//
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import com.sprint.monew.domain.interest.Interest;
//import com.sprint.monew.domain.interest.InterestRepository;
//import com.sprint.monew.domain.interest.subscription.SubscriptionRepository;
//import com.sprint.monew.global.config.QueryDSLConfig;
//import java.time.Instant;
//import java.util.List;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.context.ActiveProfiles;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = Replace.NONE)
//@ActiveProfiles("test")
//@Import(QueryDSLConfig.class)
//public class NotificationRepositoryTest {
//
//  @Autowired
//  SubscriptionRepository subscriptionRepository;
//
//  @Autowired
//  InterestRepository interestRepository;
//
//  @Test
//  void testNotificationRepository() {
//    Instant now = Instant.now();
//    subscriptionRepository.findNewArticleCountWithUserInterest(now);
//  }
//
//  @Test
//  void testInterestRepository() {
//    List<Interest> all = interestRepository.findAll();
//    System.out.println("all = " + all);
//  }
//}
