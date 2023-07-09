package com.howdev.framework.async.manager.impl;

import com.howdev.framework.async.manager.AsyncTaskManagerApi;
import com.howdev.framework.async.manager.ManagerResultEnum;
import com.howdev.framework.async.model.AsyncTaskStatus;
import com.howdev.framework.async.service.AsyncTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AsyncTaskManagerApiImpl class
 *
 * @author haozhifeng
 * @date 2023/07/02
 */
@Service
public class AsyncTaskManagerApiImpl implements AsyncTaskManagerApi {
    private final Map<String, AsyncTaskService<?, ?>> asyncTaskServiceMap = new HashMap<>();

    @Autowired
    public void buildAsyncTaskServiceMap(List<AsyncTaskService<?, ?>> asyncTaskServiceList) {
        asyncTaskServiceList.forEach(
                asyncTaskService -> asyncTaskServiceMap.put(asyncTaskService.getTaskName(), asyncTaskService)
        );
    }

    @Override
    public ManagerResultEnum startByName(String taskName) {
        AsyncTaskService<?, ?> taskService = asyncTaskServiceMap.get(taskName);
        if (taskService == null) {
            return ManagerResultEnum.TASK_NOT_FOUND;
        }
        taskService.start();
        return ManagerResultEnum.SUCCESS;
    }

    @Override
    public ManagerResultEnum stopByName(String taskName) {
        AsyncTaskService<?, ?> taskService = asyncTaskServiceMap.get(taskName);
        if (taskService == null) {
            return ManagerResultEnum.TASK_NOT_FOUND;
        }
        taskService.stop();
        return ManagerResultEnum.SUCCESS;
    }

    @Override
    public void startAll() {
        asyncTaskServiceMap.values().forEach(AsyncTaskService::start);
    }

    @Override
    public void stopAll() {
        asyncTaskServiceMap.values().forEach(AsyncTaskService::stop);
    }

    @Override
    public Map<String, AsyncTaskStatus> getAllTaskStatus() {
        Map<String, AsyncTaskStatus> asyncTaskStatusMap = new HashMap<>(4);
        asyncTaskServiceMap.values().forEach(asyncTaskService -> asyncTaskStatusMap.put(
                asyncTaskService.getTaskName(), asyncTaskService.buildAsyncTaskStatus())
        );
        return asyncTaskStatusMap;
    }

}
