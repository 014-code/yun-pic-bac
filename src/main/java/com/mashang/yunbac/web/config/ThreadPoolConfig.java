package com.mashang.yunbac.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自定义线程池配置类
 */
@Configuration
public class ThreadPoolConfig {

    @Bean(name = "commonThreadPool", destroyMethod = "shutdown")
    public ThreadPoolExecutor commonThreadPool() {
        int corePoolSize = Runtime.getRuntime().availableProcessors(); // CPU 核心数
        int maxPoolSize = corePoolSize * 2;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(1000); // 任务队列容量

        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L, TimeUnit.SECONDS, // 空闲线程存活时间
                workQueue,
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
        );
    }
}
