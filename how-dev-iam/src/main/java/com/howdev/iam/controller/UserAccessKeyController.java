package com.howdev.iam.controller;

import com.howdev.iam.annotation.ApiException;
import com.howdev.iam.dto.BaseResponse;
import com.howdev.iam.dto.UserAccessKeyReq;
import com.howdev.iam.entity.UserAccessKey;
import com.howdev.iam.entity.UserAccessKeyOperation;
import com.howdev.iam.service.UserAccessKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户访问密钥控制器
 * 处理用户访问密钥相关操作
 */
@RestController
@RequestMapping("/user/accessKey")
@RequiredArgsConstructor
@ApiException
public class UserAccessKeyController {

    private final UserAccessKeyService userAccessKeyService;

    /**
     * 申请访问密钥接口
     */
    @PostMapping("/apply")
    public BaseResponse<UserAccessKey> applyAccessKey(@RequestBody UserAccessKeyReq req) {
        UserAccessKey accessKey = userAccessKeyService.applyAccessKey(req.getUserId());
        return BaseResponse.newSuccResponse(accessKey);
    }

    /**
     * 查询访问密钥接口
     */
    @GetMapping("/list")
    public BaseResponse<List<UserAccessKey>> listAccessKeys(@RequestBody UserAccessKeyReq req) {
        List<UserAccessKey> accessKeys = userAccessKeyService.listAccessKey(req.getUserId());
        return BaseResponse.newSuccResponse(accessKeys);
    }

    /**
     * 禁用访问密钥接口
     */
    @PostMapping("/disable")
    public BaseResponse<Boolean> disableAccessKey(@RequestBody UserAccessKeyReq req) {
        boolean result = userAccessKeyService.disableAccessKey(req.getUserId(), req.getAccessKeyId());
        return BaseResponse.newSuccResponse(result);
    }

    /**
     * 启用访问密钥接口
     */
    @PostMapping("/enable")
    public BaseResponse<Boolean> enableAccessKey(@RequestBody UserAccessKeyReq req) {
        boolean result = userAccessKeyService.enableAccessKey(req.getUserId(), req.getAccessKeyId());
        return BaseResponse.newSuccResponse(result);
    }

    /**
     * 删除访问密钥接口
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteAccessKey(@RequestBody UserAccessKeyReq req) {
        boolean result = userAccessKeyService.deleteAccessKey(req.getUserId(), req.getAccessKeyId());
        return BaseResponse.newSuccResponse(result);
    }

    @GetMapping("/listOperation")
    public BaseResponse<List<UserAccessKeyOperation>> listAccessKeyOperation(@RequestBody UserAccessKeyReq req) {
        List<UserAccessKeyOperation> operations = userAccessKeyService.listAccessKeyOperation(req.getUserId());
        return BaseResponse.newSuccResponse(operations);
    }


}
