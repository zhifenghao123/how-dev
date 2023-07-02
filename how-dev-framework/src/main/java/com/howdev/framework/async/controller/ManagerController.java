package com.howdev.framework.async.controller;

import com.howdev.framework.async.aop.HttpEnabled;
import com.howdev.framework.async.manager.AsyncTaskManagerApi;
import com.howdev.framework.async.manager.ManagerResultEnum;
import com.howdev.framework.async.model.AsyncTaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ManagerController class
 *
 * @author haozhifeng
 * @date 2023/07/02
 */
@RestController
@RequestMapping("/asyncTask")
public class ManagerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerController.class);

    private static final String SUCCESS_MESSAGE = "ok";

    private static final String ERROR_MESSAGE = "error";

    private AsyncTaskManagerApi asyncTaskManagerApi;

    @Autowired
    public void setAsyncTaskManagerApi(AsyncTaskManagerApi asyncTaskManagerApi) {
        this.asyncTaskManagerApi = asyncTaskManagerApi;
    }

    @GetMapping(value = "/start")
    @ResponseBody
    @HttpEnabled
    public String startByTaskName(@RequestParam(name = "taskName") String taskName) {
        LOGGER.info("ASYNC_TASK_HTTP receive start task. taskName={}", taskName);

        try {
            // 开启任务
            ManagerResultEnum result = asyncTaskManagerApi.startByName(taskName);

            // 判断任务名是否存在
            if (result == ManagerResultEnum.TASK_NOT_FOUND) {
                LOGGER.info("ASYNC_TASK_HTTP not find task by name. taskName={}", taskName);
                return "not find taskName: " + taskName;
            }
        } catch (Exception e) {
            LOGGER.error("ASYNC_TASK_HTTP start task fail. taskName={}", taskName, e);
            return ERROR_MESSAGE;
        }
        return SUCCESS_MESSAGE;
    }

    @GetMapping(value = "/stop")
    @ResponseBody
    @HttpEnabled
    public String stopByTaskName(@RequestParam(name = "taskName") String taskName) {
        LOGGER.info("ASYNC_TASK_HTTP receive stop task. taskName={}", taskName);

        try {
            // 开启任务
            ManagerResultEnum result = asyncTaskManagerApi.stopByName(taskName);

            // 判断任务名是否存在
            if (result == ManagerResultEnum.TASK_NOT_FOUND) {
                LOGGER.info("ASYNC_TASK_HTTP not find task by name. taskName={}", taskName);
                return "not find taskName: " + taskName;
            }
        } catch (Exception e) {
            LOGGER.error("ASYNC_TASK_HTTP stop task fail. taskName={}", taskName, e);
            return ERROR_MESSAGE;
        }
        return SUCCESS_MESSAGE;
    }

    @GetMapping(value = "/startAll")
    @ResponseBody
    @HttpEnabled
    public String startAll() {
        LOGGER.info("ASYNC_TASK_HTTP receive start all task.");
        try {
            asyncTaskManagerApi.startAll();
            LOGGER.info("ASYNC_TASK_HTTP start all success.");
        } catch (Exception e) {
            LOGGER.error("ASYNC_TASK_HTTP start all fail.", e);
            return ERROR_MESSAGE;
        }
        return SUCCESS_MESSAGE;
    }

    @GetMapping(value = "/stopAll")
    @ResponseBody
    @HttpEnabled
    public String stopAll() {
        LOGGER.info("ASYNC_TASK_HTTP receive stop all task.");
        try {
            asyncTaskManagerApi.stopAll();
            LOGGER.info("ASYNC_TASK_HTTP task all success.");
        } catch (Exception e) {
            LOGGER.error("ASYNC_TASK_HTTP task all fail.", e);
            return ERROR_MESSAGE;
        }
        return SUCCESS_MESSAGE;
    }

    @GetMapping(value = "/taskStatus")
    @ResponseBody
    @HttpEnabled
    public Map<String, AsyncTaskStatus> queryTaskStatus() {
        try {
            return asyncTaskManagerApi.getAllTaskStatus();
        } catch (Exception e) {
            LOGGER.error("ASYNC_TASK_HTTP query queryTaskStatus fail.", e);
        }
        return null;
    }


}
