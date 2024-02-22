package com.howdev.app.service;

/**
 * AsyncDecisionTaskService class
 *
 * @author haozhifeng
 * @date 2024/01/31
 */
public interface AsyncDecisionTaskService {
    /**
     * 锁定任务
     *
     * @param asyncDecisionTaskId
     * @param lockIp
     * @return
     */
    boolean lockTask(Long asyncDecisionTaskId, String lockIp);
}
