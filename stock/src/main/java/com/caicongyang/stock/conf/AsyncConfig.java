package com.caicongyang.stock.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {
    @Resource
    private Environment env;

    @Bean
    public ThreadPoolTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int coreSize = Runtime.getRuntime().availableProcessors() * 2;
        int maxPoolSize = Runtime.getRuntime().availableProcessors() * 16 * 2;

        executor.setCorePoolSize(env.getProperty("stock.task.corePoolSize", int.class, coreSize));
        executor.setMaxPoolSize(env.getProperty("stock.task.maxPoolSize", int.class, maxPoolSize));
        return executor;
    }

    @Bean("asyncExecutor")
    public ExecutorService asyncExecutor() {
        return asyncTaskExecutor().getThreadPoolExecutor();
    }

    @Override
    public Executor getAsyncExecutor() {
        return asyncTaskExecutor();
    }

}
