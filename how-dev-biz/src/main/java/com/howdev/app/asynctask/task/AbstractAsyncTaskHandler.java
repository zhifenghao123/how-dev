package com.howdev.app.asynctask.task;

import com.howdev.api.model.BaseResponse;
import com.howdev.app.po.AsyncDecisionTaskPo;
import com.howdev.app.service.AsyncDecisionTaskService;
import com.howdev.framework.async.api.AsyncTaskRandomHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AbstractAsyncDecisionTask class
 *
 * @author haozhifeng
 * @date 2024/01/26
 */
@Service
public abstract class AbstractAsyncTaskHandler extends AsyncTaskRandomHandler<AsyncDecisionTaskPo, BaseResponse> {

    AsyncDecisionTaskService asyncDecisionTaskService;

    @Autowired
    public void setAsyncDecisionTaskService(AsyncDecisionTaskService asyncDecisionTaskService) {
        this.asyncDecisionTaskService = asyncDecisionTaskService;
    }

    @Override
    protected boolean lockData(AsyncDecisionTaskPo data) {
        // TODO:
        String localIp = "127.0.0.1";
        return asyncDecisionTaskService.lockTask(data.getId(), localIp);
    }

    @Override
    public void beforeTask(AsyncDecisionTaskPo data) {

    }

    @Override
    public BaseResponse executeTask(AsyncDecisionTaskPo data) {
        return null;
    }

    @Override
    public void afterTask(AsyncDecisionTaskPo data, BaseResponse result, long startTime, Throwable throwable) {

    }

}
