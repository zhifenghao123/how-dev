package com.howdev.iam.service;

import com.howdev.iam.bo.TempAccessKeyBo;
import com.howdev.iam.dto.TempAccessKeyResp;

public interface TempAccessKeyService {

    /**
     * 申请临时访问密钥
     *
     * @param userId     用户标识（用于计算分片）
     * @param expireTime 期望的过期时间戳（秒）
     * @return 包含 accessKeyId、secretAccessKey、sessionToken、expireTime 的结果
     */
    TempAccessKeyResp applyTempAccessKey(String userId, long expireTime);

    TempAccessKeyBo queryTempAccessKey(String tempAccessKeyId, String sessionToken);
}
