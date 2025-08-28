package com.mashang.yunbac;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.mashang.yunbac.web.mapper")
// 开启异步处理
@EnableAsync
public class YunBacApplication {

    public static void main(String[] args) {
        SpringApplication.run(YunBacApplication.class, args);
    }

}
