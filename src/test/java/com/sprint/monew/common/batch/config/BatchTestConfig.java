package com.sprint.monew.common.batch.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME) // 없애면 오류뜨네
@Documented
@SpringBatchTest
@EnableBatchProcessing
@EnableAutoConfiguration // Spring Boot 자동 설정(테스트 코드에서 Spring Boot가 여러 Job등록 못하게 해야하니 수동)
@ComponentScan(
    basePackages = "com.sprint.monew",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = ".*Batch$" // “Batch"로 끝나는 빈의 이름은 모두 제외(Batch클래스명 만들때 주의)
    )
)
public @interface BatchTestConfig {

}
