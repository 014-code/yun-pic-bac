package com.mashang.yunbac.web.config;

import com.mashang.yunbac.web.authlnterceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LAuthInterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor()).addPathPatterns("/*").excludePathPatterns("/user/login", "/user/register", "/doctor/notify", "/api/doc.html#/home", "/ws/**", "/swagger-ui.html", "/swagger-resources/**", "/*/api-docs", "/druid/**", "/**/*.html");
    }
}
