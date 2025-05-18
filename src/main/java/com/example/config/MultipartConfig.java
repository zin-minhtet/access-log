package com.example.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.multipart.support.MultipartFilter;

@Configuration
public class MultipartConfig {

  /**
   * Register Spring's MultipartFilter at the highest precedence so that
   * file upload requests (multipart/form-data) are parsed before any
   * other filters (including your caching/logging filter) consume the body.
   */
  @Bean
  public FilterRegistrationBean<MultipartFilter> multipartFilterRegistration() {
    FilterRegistrationBean<MultipartFilter> registration =
        new FilterRegistrationBean<>(new MultipartFilter());
    registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
    registration.addUrlPatterns("/*");
    return registration;
  }
}
