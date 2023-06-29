package com.howdev.async.async.api;

/**
 * 异步任务
 *
 * @param <T> 异步任务的入参类型
 * @param <R> 异步任务执行后的响应结果类型
 * @author haozhifeng
 * @date 2023/06/29
 */
public interface AsyncTask<T, R> {

    /**
     * 获取异步任务待执行的原始数据
     *
     * @param
     * @return: 需要执行任务的原始数据
     * @author: haozhifeng
     */
    T getTaskData();

    /**
     * 异步任务执行的前置操作
     * eg:设置日志打印的logId等
     *
     * @param data 待执行任务的原始数据
     * @return:
     * @author: haozhifeng
     */
    void beforeTask(T data);

    /**
     * 执行异步任务
     *
     * @param data 异步任务处理的原始数据
     * @return: 异步任务执行后的响应结果
     * @author: haozhifeng
     */
    R executeTask(T data);

    /**
     * 异步任务执行的后置操作
     *
     * @param data      异步任务处理的原始数据
     * @param result    异步任务执行后的响应结果
     * @param startTime 任务开始时间，毫秒时间戳
     * @param throwable 如果 executeTask 出现没有cache的未知异常，会在此处设置。否则为 null
     * @return:
     * @author: haozhifeng
     */
    void afterTask(T data, R result, long startTime, Throwable throwable);

}
