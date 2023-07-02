package com.howdev.framework.async.model;

/**
 * AsyncTaskStatus class
 *
 * @author haozhifeng
 * @date 2023/06/30
 */
public class AsyncTaskStatus {

    /**
     * 异步任务的名称
     */
    private String taskName;

    /**
     * 活跃线程数（运行中的线程数）
     */
    private int activeThreadCount;

    /**
     * 配置的核心线程数
     */
    private int configCoreThreadCount;

    /**
     * 异步任务是否运行中
     */
    private boolean ifRunning;

    /**
     * 线程获取不到任务时的等待时间
     */
    private long idleWaitTimeMillis;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getActiveThreadCount() {
        return activeThreadCount;
    }

    public void setActiveThreadCount(int activeThreadCount) {
        this.activeThreadCount = activeThreadCount;
    }

    public int getConfigCoreThreadCount() {
        return configCoreThreadCount;
    }

    public void setConfigCoreThreadCount(int configCoreThreadCount) {
        this.configCoreThreadCount = configCoreThreadCount;
    }

    public boolean isIfRunning() {
        return ifRunning;
    }

    public void setIfRunning(boolean ifRunning) {
        this.ifRunning = ifRunning;
    }

    public long getIdleWaitTimeMillis() {
        return idleWaitTimeMillis;
    }

    public void setIdleWaitTimeMillis(long idleWaitTimeMillis) {
        this.idleWaitTimeMillis = idleWaitTimeMillis;
    }
}
