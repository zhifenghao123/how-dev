package com.howdev.manage.config.thread;

import com.howdev.manage.util.thread.CallerRunsWithLogPolicy;
import com.howdev.manage.util.thread.NamedThreadFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadPoolConfigure class
 *
 * @author haozhifeng
 * @date 2023/06/08
 */
@Component
public class ThreadPoolConfigure {
    @Value("${how-dev.manage.thread-poll.calculate.core-pool-size:2}")
    private int corePoolSize;

    @Value("${how-dev.manage.thread-poll.calculate.max-pool-size:3}")
    private int maxPoolSize;

    @Value("${how-dev.manage.thread-poll.calculate.queue-capacity:15}")
    private int queueCapacity;

    @Bean
    public ExecutorService doCalculateThreadPool() {
        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new NamedThreadFactory("doCalculateThreadPool"),
                new CallerRunsWithLogPolicy()
        );
    }
}
