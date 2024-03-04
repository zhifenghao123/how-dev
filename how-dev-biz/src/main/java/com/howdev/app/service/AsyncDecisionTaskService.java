package com.howdev.app.service;

import com.howdev.app.po.AsyncDecisionTaskPo;

import java.util.List;

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

    /**
     * 查询初始状态的待执行异步任务
     *
     * @param idcTag idcTag
     * @return:
     * @author: haozhifeng
     */
    List<AsyncDecisionTaskPo> queryInitAsyncTasks(String idcTag);
}
