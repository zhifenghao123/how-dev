package com.howdev.app.asynctask.task;

import com.howdev.app.po.AsyncDecisionTaskPo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * NonInitialAsyncDecisionTask class
 *
 * @author haozhifeng
 * @date 2024/01/26
 */
@Component
public class NonInitAsyncTaskHandler extends AbstractAsyncTaskHandler {
    @Override
    protected List<AsyncDecisionTaskPo> getTaskDataList() {
        String idcTag = "idc-1";
        return asyncDecisionTaskService.queryNonInitAsyncTasks(idcTag, 10);
    }
}
