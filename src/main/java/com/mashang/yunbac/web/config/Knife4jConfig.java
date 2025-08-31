package com.mashang.yunbac.web.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("智能协同云图库 API接口文档")
                        .version("1.0")
                        .description("智能协同云图库API接口文档")
                        .contact(new Contact().name("014")))
                // 添加安全方案定义
                .components(new Components()
                        .addSecuritySchemes("Authorization", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .name("Authorization")
                                .in(SecurityScheme.In.HEADER)
                                .description("JWT认证令牌，格式: Bearer {token}")))
                // 添加全局安全要求
                .addSecurityItem(new SecurityRequirement().addList("Authorization"));
    }

    /***
     * 关键配置：创建一个包含所有接口的分组
     * 这样在Knife4j界面中会直接显示所有接口
     */
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("所有接口")
                .pathsToMatch("/**")  // 匹配所有路径
                .packagesToScan("com.mashang.yunbac.web.controller")
                .build();
    }

    /***
     * 关键修改：使用 packagesToScan 明确指定要扫描的包路径
     */
    @Bean
    public GroupedOpenApi webApi() {
        return GroupedOpenApi.builder()
                .group("web")
                .packagesToScan("com.mashang.yunbac.web.controller") // 指定控制器包路径
                .build();
    }
}
