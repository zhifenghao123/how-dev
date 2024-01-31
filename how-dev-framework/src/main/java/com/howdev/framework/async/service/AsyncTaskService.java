package com.howdev.framework.async.service;

import com.howdev.framework.async.api.AsyncTaskHandler;
import com.howdev.framework.async.model.AsyncTaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AsyncTaskService class
 *
 * @author haozhifeng
 * @date 2023/06/30
 */
public class AsyncTaskService<T, R> implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncTaskService.class);

    /**
     * 任务名称
     */
    private final String taskName;

    /**
     * 执行异步任务的线程池
     */
    private final ThreadPoolExecutor executorService;

    /**
     * 异步任务
     */
    private final AsyncTaskHandler<T, R> asyncTaskHandler;

    /**
     * 空闲时间线程执行间隔，毫秒。
     * 当任务队列获取不到新的待执行任务时，sleep之后再重新尝试获取。
     */
    private long idleWaitTimeMillis = 200;

    /**
     * 控制整个任务的开启和关闭。 true 时标识开启
     */
    private volatile boolean startSwitch = false;

    /**
     * 回收线程
     */
    private final AtomicInteger needCloseThreadCount = new AtomicInteger(0);

    /**
     * 是否关闭自动启动。
     * 如果关闭，需要手工调用start接口才会开启任务。
     */
    @Value("${common.async-task-service.close-auto-start:0}")
    private int closeAutoStart;

    public AsyncTaskService(String taskName, ThreadPoolExecutor executorService, AsyncTaskHandler<T, R> asyncTaskHandler) {
        this.taskName = taskName;
        this.executorService = executorService;
        this.asyncTaskHandler = asyncTaskHandler;
    }

    public AsyncTaskService(String taskName, ThreadPoolExecutor executorService, AsyncTaskHandler<T, R> asyncTaskHandler,
                            long idleWaitTimeMillis) {
        this(taskName, executorService, asyncTaskHandler);

        this.idleWaitTimeMillis = idleWaitTimeMillis;
        if (idleWaitTimeMillis < 0) {
            throw new IllegalArgumentException("ASYNC_SDK [" + taskName + "] init fail. idleWaitTimeMillis must >= 0. "
                    + "idleWaitTimeMillis=" + idleWaitTimeMillis);
        }
    }

    /**
     * 开启任务，按照线程池的配置开始执行
     *
     * @param
     * @return:
     * @author: haozhifeng
     */
    public synchronized void start() {

        // 获取核心线程数，作为主线程数量
        int corePoolSize = executorService.getCorePoolSize();

        if (startSwitch) {
            LOGGER.warn("ASYNC_SDK [{}] start fail. startSwitch is true.", taskName);
            return;
        }
        startSwitch = true;
        for (int i = 0; i < corePoolSize; i++) {
            executorService.submit(this::execute);
        }
        LOGGER.info("ASYNC_SDK [{}] task start. threadNumber={}.", taskName, corePoolSize);
    }

    /**
     * 关闭异步任务，设置开关为false，当前处理中的任务执行完毕时自动结束。
     *
     * @param
     * @return:
     * @author: haozhifeng
     */
    public void stop() {
        startSwitch = false;
    }

    /**
     * 线程执行
     *
     * @param
     * @return:
     * @author: haozhifeng
     */
    public void execute() {

        while (startSwitch) {
            // 业务逻辑执行
            executeTask();

            // 在通过 updateCorePoolSize 调小线程时，回收多余的线程
            if (needCloseThreadCount.get() > 0 && checkIfNeedCloseThread()) {
                LOGGER.info("ASYNC_SDK [{}] thread reduce. threadName={}.", taskName, Thread.currentThread().getName());
                break;
            }
        }
        LOGGER.info("ASYNC_SDK [{}] task end. threadName={}.", taskName, Thread.currentThread().getName());
    }

    /**
     * 执行任务
     *
     * @param
     * @return:
     * @author: haozhifeng
     */
    private void executeTask() {

        // 整体开始时间
        long startTime = System.currentTimeMillis();

        // 获取数据耗时
        long getDataCost = -1;

        // 前置任务耗时
        long beforeTaskCost = -1;

        // 主逻辑耗时
        long handleTaskCost = -1;

        // 后置任务耗时
        long afterTaskCost = -1;

        T taskData = null;
        R result = null;
        Exception runException = null;

        try {
            taskData = asyncTaskHandler.getTaskData();

            // 计算获取数据耗时
            getDataCost = System.currentTimeMillis() - startTime;

            // 未获取到数据, 跳过
            if (taskData == null) {
                // idleWaitTimeMillis 为 0 时不sleep
                if (idleWaitTimeMillis > 0) {
                    Thread.sleep(idleWaitTimeMillis);
                }
                long totalCost = System.currentTimeMillis() - startTime;
                LOGGER.info("ASYNC_SDK [{}] NO_TASK_DATA. totalCost={}, getDataCost={}, sleepCost={}",
                        taskName, totalCost, getDataCost, idleWaitTimeMillis
                );
                return;
            }

            // 任务执行前逻辑
            long beforeTaskTime = System.currentTimeMillis();
            asyncTaskHandler.beforeTask(taskData);
            beforeTaskCost = System.currentTimeMillis() - beforeTaskTime;

            // 执行任务
            long startTaskTime = System.currentTimeMillis();
            result = asyncTaskHandler.executeTask(taskData);
            handleTaskCost = System.currentTimeMillis() - startTaskTime;

        } catch (Exception e) {
            runException = e;
            LOGGER.error("ASYNC_SDK [{}] main task execution failed.", taskName, e);
        }
        try {
            long afterTaskTime = System.currentTimeMillis();
            asyncTaskHandler.afterTask(taskData, result, startTime, runException);
            afterTaskCost = System.currentTimeMillis() - afterTaskTime;
        } catch (Exception e) {
            LOGGER.error("ASYNC_SDK [{}] after task execution failed.", taskName, e);
        } finally {
            long totalCost = System.currentTimeMillis() - startTime;
            LOGGER.info(
                    "ASYNC_SDK [{}] TASK_END. totalCost={}, getDataCost={}, beforeTaskCost={}, handleTaskCost={}, "
                            + "afterTaskCost={}",
                    taskName, totalCost, getDataCost, beforeTaskCost, handleTaskCost, afterTaskCost
            );

            // 清除日志上下文
            MDC.clear();
        }
    }

    /**
     * 更新线程数量
     *
     * @param corePoolSize 更新后的线程数
     * @return:
     * @author: haozhifeng
     */
    public void updateCorePoolSize(int corePoolSize) {
        // 线程数<=0，非法
        if (corePoolSize <= 0) {
            LOGGER.error("ASYNC_SDK [{}] can't update corePoolSize. after value={}.", taskName, corePoolSize);
            return;
        }

        // 获取原有的线程数
        int oldCorePoolSize = executorService.getCorePoolSize();

        // 求出变化数
        int delta = corePoolSize - oldCorePoolSize;

        // 没有变化直接返回
        if (delta == 0) {
            return;
        }

        // 设置新的线程大小
        executorService.setCorePoolSize(corePoolSize);
        executorService.setMaximumPoolSize(corePoolSize);

        if (delta > 0) {
            // 线程数增加时，补全线程
            for (int i = 0; i < delta; i++) {
                executorService.submit(this::execute);
            }
        } else {
            // 线程数减少时，设置需要缩减的线程数量，因为不会自动停止运行中的线程
            needCloseThreadCount.set(-delta);
        }

        LOGGER.info("ASYNC_SDK [{}] update corePoolSize success. oldValue={}, newValue={}", taskName, oldCorePoolSize,
                corePoolSize);
    }

    public void updateIdleWaitTimeMillis(long idleWaitTimeMillis) {
        long oldValue = this.idleWaitTimeMillis;
        if (oldValue == idleWaitTimeMillis) {
            return;
        }
        if (idleWaitTimeMillis < 0) {
            LOGGER.error("ASYNC_SDK [{}] can't update idleWaitTimeMillis. after value={}.", taskName,
                    idleWaitTimeMillis);
            return;
        }
        this.idleWaitTimeMillis = idleWaitTimeMillis;
        LOGGER.info("ASYNC_SDK [{}] update idleWaitTimeMillis success. oldValue={}, newValue={}", taskName, oldValue,
                idleWaitTimeMillis);
    }

    /**
     * 检查是否需要关闭当前的线程。
     * 根据 needCloseThreadCount 来判断，如果 > 0，则关闭当前线程，并更新计数器。
     *
     * @param
     * @return: 是否关闭本线程
     * @author: haozhifeng
     */
    public synchronized boolean checkIfNeedCloseThread() {
        // 再次检查是否需要减少线程数量
        if (needCloseThreadCount.get() <= 0) {
            return false;
        }
        // 返回true标识本次线程需要停止，并在计数器上减1
        needCloseThreadCount.addAndGet(-1);
        return true;
    }

    /**
     * 生成任务的执行状态监控数据
     *
     * @return AsyncTaskStatus 对象，包含任务执行的各项属性
     */
    public AsyncTaskStatus buildAsyncTaskStatus() {
        AsyncTaskStatus asyncTaskStatus = new AsyncTaskStatus();
        asyncTaskStatus.setTaskName(taskName);
        asyncTaskStatus.setConfigCoreThreadCount(executorService.getCorePoolSize());
        asyncTaskStatus.setActiveThreadCount(executorService.getActiveCount());
        asyncTaskStatus.setIfRunning(startSwitch);
        asyncTaskStatus.setIdleWaitTimeMillis(idleWaitTimeMillis);
        return asyncTaskStatus;
    }

    public String getTaskName() {
        return taskName;
    }

    public ThreadPoolExecutor getExecutorService() {
        return executorService;
    }

    public AsyncTaskHandler<T, R> getAsyncTask() {
        return asyncTaskHandler;
    }

    public boolean isStartSwitch() {
        return startSwitch;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 关闭自动启动，不执行 start
        if (closeAutoStart == 1) {
            LOGGER.info("ASYNC_SDK [{}] config closeAutoStart={}, not auto start.", taskName, closeAutoStart);
            return;
        }
        start();
    }
}
