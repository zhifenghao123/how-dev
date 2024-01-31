package com.howdev.framework.async.api;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;

/**
 * 随机执行的异步任务
 *
 * @author haozhifeng
 * @date 2023/06/29
 */
public abstract class AsyncTaskRandomHandler<T, R> implements AsyncTaskHandler<T, R> {
    /**
     * 获取异步任务待执行（候选）的原始数据集合
     *
     * @param
     * @return: 异步任务待执行（候选）的原始数据集合
     * @author: haozhifeng
     */
    protected abstract List<T> getTaskDataList();

    /**
     * 锁定待执行的记录，如果使用数据库，需要对记录加锁，并返回加锁结果
     *
     * @param data 异步任务处理的原始数据
     * @return: 获取数据所结果
     * @author: haozhifeng
     */
    protected abstract boolean lockData(T data);

    /**
     * 获取异步任务待执行的原始数据
     *
     * @param
     * @return: 执行任务的原始数据
     * @author: haozhifeng
     */
    @Override
    public final T getTaskData() {
        List<T> dataList = getTaskDataList();
        if (CollectionUtils.isEmpty(dataList)) {
            return null;
        }
        T data = dataList.get(RandomUtils.nextInt(0, dataList.size()));
        if (lockData(data)) {
            return data;
        }
        return null;
    }
}
