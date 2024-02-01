package com.howdev.app.asynctask;

import com.howdev.api.model.BaseResponse;
import com.howdev.app.asynctask.task.InitAsyncTaskHandler;
import com.howdev.app.asynctask.task.NonInitAsyncTaskHandler;
import com.howdev.app.asynctask.task.RetryAsyncTaskHandler;
import com.howdev.app.po.AsyncDecisionTaskPo;
import com.howdev.framework.async.service.AsyncTaskService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * AsyncDecisionTaskConfigure class
 *
 * @author haozhifeng
 * @date 2024/01/26
 */
@Component
public class AsyncTaskServiceConfig {
    /**
     * 空闲任务等待时间，单位毫秒
     */
    @Value("${async.decision.init.thread.pool.idle-wait-time:200}")
    private int initIdleWaitTime;

    @Value("${async.decision.non-init.thread.pool.idle-wait-time:200}")
    private int nonInitIdleWaitTime;

    @Value("${async.decision.retry.thread.pool.idle-wait-time:200}")
    private int retryIdleWaitTime;

    @Bean
    public AsyncTaskService<AsyncDecisionTaskPo, BaseResponse> initialAsyncTaskService(
            ThreadPoolExecutor initialAsyncTaskThreadPool, InitAsyncTaskHandler initAsyncTaskHandler) {
        return new AsyncTaskService<>(
                "initialAsyncTaskService",
                initialAsyncTaskThreadPool,
                initAsyncTaskHandler,
                initIdleWaitTime);
    }

    @Bean
    public AsyncTaskService<AsyncDecisionTaskPo, BaseResponse> nonInitialAsyncTaskService(
            ThreadPoolExecutor nonInitialAsyncTaskThreadPool, NonInitAsyncTaskHandler nonInitAsyncTaskHandler) {
        return new AsyncTaskService<>(
                "nonInitialAsyncTaskService",
                nonInitialAsyncTaskThreadPool,
                nonInitAsyncTaskHandler,
                nonInitIdleWaitTime);
    }

    @Bean
    public AsyncTaskService<AsyncDecisionTaskPo, BaseResponse> retryAsyncTaskService(
            ThreadPoolExecutor retryAsyncTaskThreadPool, RetryAsyncTaskHandler retryAsyncTaskHandler) {
        return new AsyncTaskService<>(
                "retryAsyncTaskService",
                retryAsyncTaskThreadPool,
                retryAsyncTaskHandler,
                retryIdleWaitTime);
    }

}
