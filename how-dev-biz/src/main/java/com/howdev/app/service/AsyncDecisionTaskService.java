package com.howdev.app.service;

import com.howdev.app.po.AsyncDecisionTaskPo;

/**
 * AsyncDecisionTaskService class
 *
 * @author haozhifeng
 * @date 2024/01/31
 */
public interface AsyncDecisionTaskService {
    /**
     * 锁定任务
     * @param asyncDecisionTask
     * @return
     */
    boolean lockTask(AsyncDecisionTaskPo asyncDecisionTask);
}
