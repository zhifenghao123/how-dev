package com.howdev.iam.service.impl;

import com.howdev.iam.constant.UserAccessKeyConst;
import com.howdev.iam.entity.UserAccessKey;
import com.howdev.iam.entity.UserAccessKeyOperation;
import com.howdev.iam.enumeration.RetCodeEnum;
import com.howdev.iam.exception.RetCodeException;
import com.howdev.iam.handler.AccessKeyGenerator;
import com.howdev.iam.handler.AttributeEncryptDesUtil;
import com.howdev.iam.repository.UserAccessKeyOperationRepository;
import com.howdev.iam.repository.UserAccessKeyRepository;
import com.howdev.iam.service.UserAccessKeyService;
import com.howdev.iam.util.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BinaryOperator;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAccessKeyServiceImpl implements UserAccessKeyService {

    private final UserAccessKeyRepository userAccessKeyRepository;
    private final UserAccessKeyOperationRepository userAccessKeyOperationRepository;

    @Override
    public UserAccessKey applyAccessKey(String userId) {
        // 检查用户是否已经存在访问密钥
        List<UserAccessKey> existedAccessKeys = userAccessKeyRepository.findByUserIdAndStatus(userId, UserAccessKeyConst.ACCESS_KEY_STATUS_ENABLE);
        if (CollectionUtils.isNotEmpty(existedAccessKeys)) {
            log.warn("用户 {} 已经存在访问密钥", userId);
            throw new RetCodeException(RetCodeEnum.ILLEGAL_ARGUMENT, "用户已经存在访问密钥");
        }

        LocalDateTime now = LocalDateTime.now();
        long timestamp = now.atZone(ZoneId.of("Asia/Shanghai"))
                .toInstant()
                .toEpochMilli();


        // 生成访问密钥信息
        String accessKeyId = AccessKeyGenerator.generateAccessKeyId();
        String secretAccessKey = AccessKeyGenerator.generateSecretAccessKey();
        String encryptedSecretKey = AttributeEncryptDesUtil.encrypt(secretAccessKey, userId.hashCode(), timestamp);

        // 创建访问密钥实体
        UserAccessKey userAccessKey = new UserAccessKey();
        userAccessKey.setUserId(userId);
        userAccessKey.setAccessKeyId(accessKeyId);
        userAccessKey.setSecretAccessKey(encryptedSecretKey);
        userAccessKey.setStatus("ENABLE");
        userAccessKey.setCreateTime(now);
        userAccessKey.setUpdateTime(now);

        try {
            // 保存访问密钥
            UserAccessKey savedAccessKey = userAccessKeyRepository.save(userAccessKey);
            log.info("用户 {} 申请访问密钥成功，accessKeyId: {}", userId, accessKeyId);
            
            // 记录操作流水
            recordOperation(userId, accessKeyId, "APPLY");
            
            // 返回包含明文密钥的实体（仅在此处返回明文）
            savedAccessKey.setSecretAccessKey(secretAccessKey);
            return savedAccessKey;
        } catch (Exception e) {
            log.error("保存访问密钥失败: {}", e.getMessage());
            throw new RetCodeException(RetCodeEnum.FAILED, "申请访问密钥失败", e.getMessage());
        }
    }

    /**
     * 记录操作流水
     */
    private void recordOperation(String userId, String accessKeyId, String operation) {
        UserAccessKeyOperation operationRecord = new UserAccessKeyOperation();
        operationRecord.setUserId(userId);
        operationRecord.setAccessKeyId(accessKeyId);
        operationRecord.setOperation(operation);
        operationRecord.setCreateTime(LocalDateTime.now());
        
        userAccessKeyOperationRepository.save(operationRecord);
        log.info("记录访问密钥操作流水: userId={}, accessKeyId={}, operation={}", userId, accessKeyId, operation);
    }

    @Override
    public List<UserAccessKey> listAccessKey(String userId) {
        List<UserAccessKey> accessKeys = userAccessKeyRepository.findByUserIdAndStatus(userId, UserAccessKeyConst.ACCESS_KEY_STATUS_ENABLE);
        if (CollectionUtils.isNotEmpty(accessKeys)) {
            for (UserAccessKey accessKey : accessKeys) {
                long timestamp = accessKey.getCreateTime().atZone(ZoneId.of("Asia/Shanghai"))
                        .toInstant()
                        .toEpochMilli();
                String secretAccessKey = AttributeEncryptDesUtil.decrypt(accessKey.getSecretAccessKey(), userId.hashCode(), timestamp);
                accessKey.setSecretAccessKey(secretAccessKey);
            }
        }
        return accessKeys;
    }

    @Override
    public UserAccessKey queryAccessKeyByAccessKeyId(String accessKeyId) {
        UserAccessKey accessKey = userAccessKeyRepository.findByAccessKeyId(accessKeyId);
        if (accessKey == null) {
            throw new RetCodeException(RetCodeEnum.ILLEGAL_ARGUMENT, "访问密钥不存在");
        }
        return accessKey;
    }

    @Override
    public Boolean disableAccessKey(String userId, String accessKeyId) {
        UserAccessKey accessKey = userAccessKeyRepository.findByUserIdAndAccessKeyId(userId, accessKeyId);
        if (accessKey == null) {
            throw new RetCodeException(RetCodeEnum.ILLEGAL_ARGUMENT, "访问密钥不存在");
        }
        accessKey.setStatus(UserAccessKeyConst.ACCESS_KEY_STATUS_DISABLE);
        accessKey.setUpdateTime(LocalDateTime.now());
        userAccessKeyRepository.save(accessKey);
        recordOperation(accessKey.getUserId(), accessKeyId, UserAccessKeyConst.ACCESS_KEY_OPERATION_DISABLE);
        return true;
    }

    @Override
    public Boolean enableAccessKey(String userId, String accessKeyId) {
        UserAccessKey accessKey = userAccessKeyRepository.findByUserIdAndAccessKeyId(userId, accessKeyId);
        if (accessKey == null) {
            throw new RetCodeException(RetCodeEnum.ILLEGAL_ARGUMENT, "访问密钥不存在");
        }
        accessKey.setStatus(UserAccessKeyConst.ACCESS_KEY_STATUS_ENABLE);
        accessKey.setUpdateTime(LocalDateTime.now());
        userAccessKeyRepository.save(accessKey);
        recordOperation(accessKey.getUserId(), accessKeyId, UserAccessKeyConst.ACCESS_KEY_OPERATION_ENABLE);
        return true;
    }

    @Override
    public Boolean deleteAccessKey(String userId, String accessKeyId) {
        UserAccessKey accessKey = userAccessKeyRepository.findByUserIdAndAccessKeyId(userId, accessKeyId);
        if (accessKey == null) {
            throw new RetCodeException(RetCodeEnum.ILLEGAL_ARGUMENT, "访问密钥不存在");
        }
        userAccessKeyRepository.delete(accessKey);
        recordOperation(accessKey.getUserId(), accessKeyId, UserAccessKeyConst.ACCESS_KEY_OPERATION_DELETE);
        return true;
    }

    @Override
    public List<UserAccessKeyOperation> listAccessKeyOperation(String userId) {
        return userAccessKeyOperationRepository.findByUserId(userId);
    }

}