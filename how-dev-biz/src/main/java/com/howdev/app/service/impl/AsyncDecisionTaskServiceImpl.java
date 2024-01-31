package com.howdev.app.service.impl;

import com.howdev.app.po.AsyncDecisionTaskPo;
import com.howdev.app.service.AsyncDecisionTaskService;
import org.springframework.stereotype.Service;

/**
 * AsyncDecisionTaskServiceImpl class
 *
 * @author haozhifeng
 * @date 2024/01/31
 */
@Service
public class AsyncDecisionTaskServiceImpl implements AsyncDecisionTaskService {

    @Override
    public boolean lockTask(AsyncDecisionTaskPo asyncDecisionTask) {
        return false;
    }
}