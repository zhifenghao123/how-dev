package com.howdev.app.util.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * CallerRunsWithLogPolicy class
 *
 * @author haozhifeng
 * @date 2024/01/26
 */
public class CallerRunsWithLogPolicy implements RejectedExecutionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CallerRunsWithLogPolicy.class);

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        int poorSize = executor.getPoolSize();
        int activeCount = executor.getActiveCount();
        int queueSize = executor.getQueue().size();

        LOGGER.error("Triggering Task Denial Policy! poorSize: {}, activeCount: {}, queueSize: {}. task: {}",
                poorSize, activeCount, queueSize, r);

        // 调用者线程(主线程)执行
        if (!executor.isShutdown()) {
            r.run();
        }
    }
}
