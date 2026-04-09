package com.howdev.iam.service;

import com.howdev.iam.entity.UserAccessKey;
import com.howdev.iam.entity.UserAccessKeyOperation;

import java.util.List;

/**
 * 用户访问密钥服务接口
 */
public interface UserAccessKeyService {

    /**
     * 申请访问密钥
     * @param userId 用户ID
     * @return 生成的访问密钥信息
     */
    UserAccessKey applyAccessKey(String userId);

    List<UserAccessKey> listAccessKey(String userId);

    UserAccessKey queryAccessKeyByAccessKeyId(String accessKeyId);

    Boolean disableAccessKey(String userId, String accessKeyId);

    Boolean enableAccessKey(String userId, String accessKeyId);

    Boolean deleteAccessKey(String userId, String accessKeyId);

    List<UserAccessKeyOperation> listAccessKeyOperation(String userId);

}