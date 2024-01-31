package com.howdev.app.util.thread;

import org.springframework.lang.NonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * NamedThreadFactory class
 *
 * @author haozhifeng
 * @date 2024/01/26
 */
public class NamedThreadFactory implements ThreadFactory {
    private static final AtomicInteger POOL_SEQ = new AtomicInteger(1);

    private final AtomicInteger threadNum = new AtomicInteger(1);

    /**
     * 线程名前缀
     */
    private final String prefix;

    /**
     * 是否为守护线程
     */
    private final boolean daemon;

    /**
     * 线程组
     */
    private final ThreadGroup group;

    public NamedThreadFactory() {
        this("pool-" + POOL_SEQ.getAndIncrement(), false);
    }


    /**
     * NamedThreadFactory构造函数
     *
     * @param prefix 线程名前缀
     * @return:
     * @author: haozhifeng
     */
    public NamedThreadFactory(String prefix) {
        this(prefix, false);
    }

    /**
     * NamedThreadFactory构造函数
     *
     * @param prefix 线程名前缀
     * @param daemon 是否为守护线程
     */
    public NamedThreadFactory(String prefix, boolean daemon) {
        this.prefix = prefix + "-thread-";
        this.daemon = daemon;
        SecurityManager s = System.getSecurityManager();
        this.group = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
    }

    @Override
    public Thread newThread(@NonNull Runnable runnable) {
        String name = this.prefix + this.threadNum.getAndIncrement();
        Thread ret = new Thread(this.group, runnable, name, 0);
        ret.setDaemon(this.daemon);
        return ret;
    }
}
