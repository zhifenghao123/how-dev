package com.howdev.iam.controller;

import com.howdev.iam.annotation.ApiException;
import com.howdev.iam.dto.BaseResponse;
import com.howdev.iam.task.ExpiredTempAccessKeyCleanTask;
import com.howdev.iam.task.TempAccessKeyGenerateTask;
import com.howdev.iam.task.TempAccessKeySeedGenerateTask;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
@ApiException
public class TaskController {
    private final ExpiredTempAccessKeyCleanTask expiredTempAccessKeyCleanTask;
    private final TempAccessKeyGenerateTask tempAccessKeyGenerateTask;
    private final TempAccessKeySeedGenerateTask tempAccessKeySeedGenerateTask;


    @PostMapping("/expiredTempAccessKeyClean")
    public BaseResponse<Void> expiredTempAccessKeyClean() {
        expiredTempAccessKeyCleanTask.execute();
        return BaseResponse.newSuccResponse(null);
    }

    @PostMapping("/tempAccessKeySeedGenerate")
    public BaseResponse<Void> tempAccessKeySeedGenerate() {
        tempAccessKeySeedGenerateTask.execute();
        return BaseResponse.newSuccResponse(null);
    }

    @PostMapping("/tempAccessKeyGenerate")
    public BaseResponse<Void> tempAccessKeyGenerate() {
        tempAccessKeyGenerateTask.execute();
        return BaseResponse.newSuccResponse(null);
    }

}
