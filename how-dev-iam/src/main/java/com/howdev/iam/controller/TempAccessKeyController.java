package com.howdev.iam.controller;

import com.howdev.iam.annotation.ApiException;
import com.howdev.iam.bo.TempAccessKeyBo;
import com.howdev.iam.bo.TempAccessKeySeedBo;
import com.howdev.iam.dto.BaseResponse;
import com.howdev.iam.dto.UserApplyTempAccessKeyReq;
import com.howdev.iam.dto.TempAccessKeyResp;
import com.howdev.iam.dto.UserQueryTempAccessKeyReq;
import com.howdev.iam.service.TempAccessKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户访问密钥控制器
 * 处理用户访问密钥相关操作
 */
@RestController
@RequestMapping("/user/tempAccessKey")
@RequiredArgsConstructor
@ApiException
public class TempAccessKeyController {
    private final TempAccessKeyService tempAccessKeyService;

    /**
     * 申请访问密钥接口
     */
    @PostMapping("/apply")
    public BaseResponse<TempAccessKeyResp> applyAccessKey(@RequestBody UserApplyTempAccessKeyReq req) {
        TempAccessKeyResp accessKey = tempAccessKeyService.applyTempAccessKey(req.getUserId(), req.getDurationSeconds());
        return BaseResponse.newSuccResponse(accessKey);
    }

    @PostMapping("/query")
    public BaseResponse<TempAccessKeyBo> queryAccessKey(@RequestBody UserQueryTempAccessKeyReq req) {
        TempAccessKeyBo accessKey = tempAccessKeyService.queryTempAccessKey(req.getTempAccessKeyId(), req.getSessionToken());
        return BaseResponse.newSuccResponse(accessKey);
    }

}
