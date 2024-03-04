package com.howdev.app.service.impl;

import com.howdev.app.mapper.AsyncDecisionTaskMapper;
import com.howdev.app.po.AsyncDecisionTaskPo;
import com.howdev.app.service.AsyncDecisionTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AsyncDecisionTaskServiceImpl class
 *
 * @author haozhifeng
 * @date 2024/01/31
 */
@Service
public class AsyncDecisionTaskServiceImpl implements AsyncDecisionTaskService {
    @Autowired
    private AsyncDecisionTaskMapper asyncDecisionTaskMapper;

    @Override
    public boolean lockTask(Long asyncDecisionTaskId, String lockIp) {
        return false;
    }

    @Override
    public List<AsyncDecisionTaskPo> queryInitAsyncTasks(String idcTag) {
        List<AsyncDecisionTaskPo> initAsyncTasks = asyncDecisionTaskMapper.queryInitAsyncTasks(idcTag, 10);
        return initAsyncTasks;
    }
}
