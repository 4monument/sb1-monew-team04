package com.sprint.monew.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    // SPA 라우팅 설정
    registry.addViewController("/").setViewName("forward:/index.html");
    registry.addViewController("/users").setViewName("forward:/index.html");
    registry.addViewController("/api/user-activities/**").setViewName("forward:/index.html");

    // 정적 리소스 경로는 따로 처리하도록 수정
    registry.addViewController("/static/**").setViewName("forward:/static/");
  }
}
