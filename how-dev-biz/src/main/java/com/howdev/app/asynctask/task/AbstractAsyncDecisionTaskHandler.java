package com.howdev.app.asynctask.task;

import com.howdev.api.model.BaseResponse;
import com.howdev.app.po.AsyncDecisionTaskPo;
import com.howdev.framework.async.api.AsyncTaskRandomHandler;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AbstractAsyncDecisionTask class
 *
 * @author haozhifeng
 * @date 2024/01/26
 */
@Service
public class AbstractAsyncDecisionTaskHandler extends AsyncTaskRandomHandler<AsyncDecisionTaskPo, BaseResponse> {
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

    @Override
    protected List<AsyncDecisionTaskPo> getTaskDataList() {
        return null;
    }

    @Override
    protected boolean lockData(AsyncDecisionTaskPo data) {
        return false;
    }
}
