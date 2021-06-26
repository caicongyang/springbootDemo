package com.caicongyang.stock.component;/*
package com.caicongyang.component;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@EnableAsync
@Component
public class TaskPoolConfig {


    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(1024);

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("taskExecutor-");

        executor.setRejectedExecutionHandler(new CallerRunsPolicy());
        executor.setAwaitTerminationSeconds(60);

        executor.initialize();
        return executor;
    }

}
*/
