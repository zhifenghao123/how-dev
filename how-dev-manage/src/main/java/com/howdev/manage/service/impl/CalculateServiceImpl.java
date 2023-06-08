package com.howdev.manage.service.impl;

import com.howdev.manage.service.CalculateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * CalculateServiceImpl class
 *
 * @author haozhifeng
 * @date 2023/06/08
 */
@Service
public class CalculateServiceImpl implements CalculateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CalculateServiceImpl.class);
    private ExecutorService executorService;

    @Autowired
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public int calculate() {
        List<Callable<Integer>> callables = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            int finalI = i;
            Callable<Integer> callable = () -> doCalculate(finalI * 100, (finalI +1) * 100 -1);
            callables.add(callable);
        }

        List<Future<Integer>> futures = null;
        try {
            futures = executorService.invokeAll(callables);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int result = 0;
        for (Future<Integer> future : futures) {
            try {
                result += future.get();
                System.out.println("future task: " + future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        //executorService.shutdown();


        return result;
    }

    private int doCalculate(int start, int end) {
       // long startTime = System.currentTimeMillis();
        LOGGER.info("start calculate. threadName={}.", Thread.currentThread().getName());
        int sum =0;
        for (int i = start; i < end; i++) {
            sum += i;
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //long totalCost = System.currentTimeMillis() - startTime;
        //LOGGER.info("end calculate, threadName={}, total cost time ={}.", Thread.currentThread().getName(), totalCost);
        return sum;
    }

    @Override
    public int calculate2() {
        List<Future<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            int finalI = i;
            Callable<Integer> callable = () -> doCalculate(finalI * 100, (finalI +1) * 100 -1);
            Future<Integer> future = executorService.submit(callable);
            futures.add(future);
        }


        int result = 0;
        for (Future<Integer> future : futures) {
            try {
                result += future.get();
                System.out.println("future task: " + future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        //executorService.shutdown();


        return result;
    }
}
