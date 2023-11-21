package com.howdev.test.shutdownhook;

import java.util.Date;

/**
 * HowShutdownHook class
 *
 * @author haozhifeng
 * @date 2023/11/21
 */
public class HowShutdownHook {
    public static void main(String[] args) throws InterruptedException {
        System.out.println(HowShutdownHook.class.getName() + " process is running. current time is:" + new Date());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println(HowShutdownHook.class.getName() + "process is down. current time is:" + new Date());
        }));

        Thread.sleep(Long.MAX_VALUE);
    }
}
