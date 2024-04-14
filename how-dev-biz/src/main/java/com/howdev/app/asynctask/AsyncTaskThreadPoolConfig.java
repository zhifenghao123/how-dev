package com.howdev.app.asynctask;

import com.howdev.app.util.thread.CallerRunsWithLogPolicy;
import com.howdev.app.util.thread.NamedThreadFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * AsyncTaskThreadPoolConfigure class
 *
 * @author haozhifeng
 * @date 2024/01/26
 */
@Component
public class AsyncTaskThreadPoolConfig {
    /**
     *  异步任务线程池大小
     */
    @Value("${async.decision.init.thread.pool.exceptions-pool-size:10}")
    private int initCorePoolSize;

    /**
     *  异步任务线程池最大线程数
     */
    @Value("${async.decision.init.thread.pool.max-pool-size:20}")
    private int initMaxPoolSize;

    /**
     *  异步任务线程池队列容量
     */
    @Value("$async.decision.init.thread.pool.queue-capacity:50}")
    private int initQueueCapacity;


    /**
     *  异步任务线程池大小
     */
    @Value("${async.decision.init.thread.pool.exceptions-pool-size:10}")
    private int nonInitCorePoolSize;

    /**
     *  异步任务线程池最大线程数
     */
    @Value("${async.decision.non-init.thread.pool.max-pool-size:20}")
    private int nonInitMaxPoolSize;

    /**
     *  异步任务线程池队列容量
     */
    @Value("$async.decision.non-init.thread.pool.queue-capacity:50}")
    private int nonInitQueueCapacity;


    /**
     *  异步任务线程池大小
     */
    @Value("${async.decision.non-init.thread.pool.exceptions-pool-size:10}")
    private int retryCorePoolSize;

    /**
     *  异步任务线程池最大线程数
     */
    @Value("${async.decision.non-init.thread.pool.max-pool-size:20}")
    private int retryMaxPoolSize;

    /**
     *  异步任务线程池队列容量
     */
    @Value("$async.decision.non-init.thread.pool.queue-capacity:50}")
    private int retryQueueCapacity;


    /**
     * 初始阶段的异步决策任务线程池
     * @return
     */
    @Bean
    public ThreadPoolExecutor initialAsyncTaskThreadPool() {
        return new ThreadPoolExecutor(
                initCorePoolSize,
                initMaxPoolSize,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(initQueueCapacity),
                new NamedThreadFactory(" initialAsyncTaskThreadPool"),
                new CallerRunsWithLogPolicy());
    }

    /**
     * 非初始阶段的异步决策任务线程池
     * @return
     */
    @Bean
    public ThreadPoolExecutor nonInitialAsyncTaskThreadPool() {
        return new ThreadPoolExecutor(
                nonInitCorePoolSize,
                nonInitMaxPoolSize,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(nonInitQueueCapacity),
                new NamedThreadFactory(" nonInitialAsyncTaskThreadPool"),
                new CallerRunsWithLogPolicy());
    }

    /**
     * 重试的异步决策任务线程池
     * @return
     */
    @Bean
    public ThreadPoolExecutor retryAsyncTaskThreadPool() {
        return new ThreadPoolExecutor(
                retryCorePoolSize,
                retryMaxPoolSize,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(retryQueueCapacity),
                new NamedThreadFactory(" retryAsyncTaskThreadPool"),
                new CallerRunsWithLogPolicy());
    }
}
