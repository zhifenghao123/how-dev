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
public class AsyncDecisionThreadPoolConfigure {
    /**
     *  异步任务线程池大小
     */
    @Value("${async.decision.initial-decision.thread-pool.core-pool-size:10}")
    private int initialDecisionCorePoolSize;

    /**
     *  异步任务线程池最大线程数
     */
    @Value("${async.decision.initial-decision.thread.pool.max-pool-size:20}")
    private int initialDecisionMaxPoolSize;

    /**
     *  异步任务线程池队列容量
     */
    @Value("$async.decision.initial-decision.thread.pool.queue-capacity:50}")
    private int initialDecisionQueueCapacity;


    /**
     *  异步任务线程池大小
     */
    @Value("${async.decision.non-initial-decision.thread-pool.core-pool-size:10}")
    private int nonInitialDecisionCorePoolSize;

    /**
     *  异步任务线程池最大线程数
     */
    @Value("${async.decision.non-initial-decision.thread.pool.max-pool-size:20}")
    private int nonInitialDecisionMaxPoolSize;

    /**
     *  异步任务线程池队列容量
     */
    @Value("$async.decision.non-initial-decision.thread.pool.queue-capacity:50}")
    private int nonInitialDecisionQueueCapacity;


    /**
     *  异步任务线程池大小
     */
    @Value("${async.decision.retry-decision.thread-pool.core-pool-size:10}")
    private int retryDecisionCorePoolSize;

    /**
     *  异步任务线程池最大线程数
     */
    @Value("${async.decision.retry-decision.thread.pool.max-pool-size:20}")
    private int retryDecisionMaxPoolSize;

    /**
     *  异步任务线程池队列容量
     */
    @Value("$async.decision.retry-decision.thread.pool.queue-capacity:50}")
    private int retryDecisionQueueCapacity;


    /**
     * 初始阶段的异步决策任务线程池
     * @return
     */
    @Bean
    public ThreadPoolExecutor initialDecisionThreadPool() {
        return new ThreadPoolExecutor(
                initialDecisionCorePoolSize,
                initialDecisionMaxPoolSize,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(initialDecisionQueueCapacity),
                new NamedThreadFactory(" InitialDecisionTask"),
                new CallerRunsWithLogPolicy());
    }

    /**
     * 非初始阶段的异步决策任务线程池
     * @return
     */
    @Bean
    public ThreadPoolExecutor nonInitialDecisionThreadPool() {
        return new ThreadPoolExecutor(
                nonInitialDecisionCorePoolSize,
                nonInitialDecisionMaxPoolSize,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(nonInitialDecisionQueueCapacity),
                new NamedThreadFactory(" NonInitialDecisionTask"),
                new CallerRunsWithLogPolicy());
    }

    /**
     * 重试的异步决策任务线程池
     * @return
     */
    @Bean
    public ThreadPoolExecutor retryDecisionThreadPool() {
        return new ThreadPoolExecutor(
                retryDecisionCorePoolSize,
                retryDecisionMaxPoolSize,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(retryDecisionQueueCapacity),
                new NamedThreadFactory(" RetryDecisionTask"),
                new CallerRunsWithLogPolicy());
    }
}
