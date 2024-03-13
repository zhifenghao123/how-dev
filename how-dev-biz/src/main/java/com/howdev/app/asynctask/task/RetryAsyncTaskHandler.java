package com.howdev.app.asynctask.task;

import com.howdev.app.po.AsyncDecisionTaskPo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * RetryAsyncDecisionTask class
 *
 * @author haozhifeng
 * @date 2024/01/26
 */
@Component
public class RetryAsyncTaskHandler extends AbstractAsyncTaskHandler {
    @Override
    protected List<AsyncDecisionTaskPo> getTaskDataList() {
        return asyncDecisionTaskService.queryRetryTask();
    }
}
