package com.howdev.app.asynctask;

import com.howdev.api.model.BaseResponse;
import com.howdev.app.asynctask.task.InitialAsyncDecisionTaskHandler;
import com.howdev.app.asynctask.task.NonInitialAsyncDecisionTaskHandler;
import com.howdev.app.asynctask.task.RetryAsyncDecisionTaskHandler;
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
public class AsyncDecisionServiceConfigure {
    /**
     * 空闲任务等待时间，单位毫秒
     */
    @Value("${async.decision.initial-decision.thread.pool.idle-wait-time:200}")
    private int initialDecisionIdleWaitTime;

    @Value("${async.decision.non-initial-decision.thread.pool.idle-wait-time:200}")
    private int nonInitialDecisionIdleWaitTime;

    @Value("${async.decision.retry-decision.thread.pool.idle-wait-time:200}")
    private int retryDecisionIdleWaitTime;

    @Bean
    public AsyncTaskService<AsyncDecisionTaskPo, BaseResponse> initialAsyncDecisionTaskService(
            ThreadPoolExecutor initialDecisionThreadPool, InitialAsyncDecisionTaskHandler initialAsyncDecisionTask) {
        return new AsyncTaskService<>(
                "initialAsyncDecisionTaskService",
                initialDecisionThreadPool,
                initialAsyncDecisionTask,
                initialDecisionIdleWaitTime);
    }

    @Bean
    public AsyncTaskService<AsyncDecisionTaskPo, BaseResponse> nonInitialAsyncDecisionTaskService(
            ThreadPoolExecutor nonInitialDecisionThreadPool, NonInitialAsyncDecisionTaskHandler nonInitialAsyncDecisionTask) {
        return new AsyncTaskService<>(
                "nonInitialAsyncDecisionTaskService",
                nonInitialDecisionThreadPool,
                nonInitialAsyncDecisionTask,
                nonInitialDecisionIdleWaitTime);
    }

    @Bean
    public AsyncTaskService<AsyncDecisionTaskPo, BaseResponse> retryAsyncDecisionTaskService(
            ThreadPoolExecutor retryDecisionThreadPool, RetryAsyncDecisionTaskHandler retryAsyncDecisionTask) {
        return new AsyncTaskService<>(
                "retryAsyncDecisionTaskService",
                retryDecisionThreadPool,
                retryAsyncDecisionTask,
                retryDecisionIdleWaitTime);
    }

}
