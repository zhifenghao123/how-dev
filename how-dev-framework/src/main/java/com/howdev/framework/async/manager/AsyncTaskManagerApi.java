package com.howdev.framework.async.manager;

import com.howdev.framework.async.model.AsyncTaskStatus;

import java.util.Map;

/**
 * AsyncTaskManagerApi class
 *
 * @author haozhifeng
 * @date 2023/06/30
 */
public interface AsyncTaskManagerApi {

    /**
     * 按照任务名称开启任务
     *
     * @param taskName 任务名称
     * @return: 执行结果
     * @author: haozhifeng
     */
    ManagerResultEnum startByName(String taskName);

    /**
     * 按照任务名称停止任务
     *
     * @param taskName 任务名称
     * @return: 执行结果
     * @author: haozhifeng
     */
    ManagerResultEnum stopByName(String taskName);

    /**
     * 开启所有的任务，如果已经运行中则忽略
     *
     * @param
     * @return:
     * @author: haozhifeng
     */
    void startAll();

    /**
     * 停止所有的任务，如果已经停止则忽略
     *
     * @param
     * @return:
     * @author: haozhifeng
     */
    void stopAll();

    /**
     * 返回所有任务的状态，key为任务名称
     *
     * @param
     * @return:
     * @author: haozhifeng
     */
    Map<String, AsyncTaskStatus> getAllTaskStatus();
}
