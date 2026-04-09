package com.howdev.iam.repository;

import com.howdev.iam.entity.UserAccessKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户访问密钥数据访问层
 */
@Repository
public interface UserAccessKeyRepository extends JpaRepository<UserAccessKey, Long> {

    /**
     * 根据访问密钥ID查找访问密钥
     */
    UserAccessKey findByUserIdAndAccessKeyId(String userId, String accessKeyId);

    /**
     * 检查访问密钥ID是否存在
     */
    boolean existsByAccessKeyId(String accessKeyId);

    /**
     * 根据用户ID查找访问密钥
     */
    List<UserAccessKey> findByUserIdAndStatus(String userId, String status);

    /**
     * 根据 AccessKeyId 查找访问密钥（用于签名验证时根据 AccessKeyId 反查密钥）
     */
    UserAccessKey findByAccessKeyId(String accessKeyId);

}