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
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SpringBatchTest
@EnableBatchProcessing                             // Batch infrastructure 활성화
@EnableAutoConfiguration                           // Spring Boot 자동 설정 로드
@ComponentScan(
    basePackages = "com.sprint.monew",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = ".*Batch.*" // “Batch” 이름이 들어간 빈은 모두 제외
    )
)
public @interface BatchTestConfig {

}
