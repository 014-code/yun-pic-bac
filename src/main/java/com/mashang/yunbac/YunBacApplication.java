package com.mashang.yunbac;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.mashang.yunbac.web.mapper")
public class YunBacApplication {

    public static void main(String[] args) {
        SpringApplication.run(YunBacApplication.class, args);
    }

}
