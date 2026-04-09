package com.howdev.iam.repository;

import com.howdev.iam.entity.TempAccessKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 临时访问密钥数据访问层
 */
@Repository
public interface TempAccessKeyRepository extends JpaRepository<TempAccessKey, Long> {

    /**
     * 根据临时访问密钥ID查找
     */
    TempAccessKey findByTempAccessKeyId(String tempAccessKeyId);

    /**
     * 删除过期的临时访问密钥
     */
    void deleteByKeyExpireTimeLessThan(Long expireTime);

    /**
     * 查找过期的临时访问密钥
     */
    List<TempAccessKey> findByKeyExpireTimeLessThan(Long expireTime);

    /**
     * 根据分片标识查询满足条件的临时凭证：
     * distribute_base 匹配、过期时间大于指定值、创建时间小于指定值
     */
    TempAccessKey findFirstByDistributeBaseAndKeyExpireTimeGreaterThanAndKeyCreateTimeLessThan(
            Integer distributeBase, Long keyExpireTime, Long keyCreateTime);

}
