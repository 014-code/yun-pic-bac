package com.mashang.yunbac.web.config;

import com.mashang.yunbac.web.authlnterceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import javax.annotation.Resource;

@Configuration
public class LAuthInterceptorConfig implements WebMvcConfigurer {

    @Resource
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/doc.html",
                        "/ws/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/*/api-docs",
                        "/druid/**",
                        "/**/*.html"
                );
    }
}
