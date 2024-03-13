package com.howdev.app.asynctask.task;

import com.howdev.app.po.AsyncDecisionTaskPo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * InitialAsyncDecisionTask class
 *
 * @author haozhifeng
 * @date 2024/01/26
 */
@Service
public class InitAsyncTaskHandler extends AbstractAsyncTaskHandler {

    @Override
    protected List<AsyncDecisionTaskPo> getTaskDataList() {
        String idcTag = "idc-1";
        return asyncDecisionTaskService.queryInitAsyncTasks(idcTag, 10);
    }
}
