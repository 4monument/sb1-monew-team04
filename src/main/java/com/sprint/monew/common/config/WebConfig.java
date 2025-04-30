package com.sprint.monew.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    // 새로고침시 오류 안나게 설정
    registry.addViewController("/").setViewName("forward:/index.html");
    registry.addViewController("/login").setViewName("forward:/index.html");
    registry.addViewController("/signup").setViewName("forward:/index.html");
    registry.addViewController("/articles").setViewName("forward:/index.html");
    registry.addViewController("/interests").setViewName("forward:/index.html");
    registry.addViewController("/articles").setViewName("forward:/index.html");
    registry.addViewController("/user-activities").setViewName("forward:/index.html");
  }
}
