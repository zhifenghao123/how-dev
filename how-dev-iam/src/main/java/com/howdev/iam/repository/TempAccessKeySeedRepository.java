package com.howdev.iam.repository;

import com.howdev.iam.entity.TempAccessKeySeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 临时访问密钥种子数据访问层
 */
@Repository
public interface TempAccessKeySeedRepository extends JpaRepository<TempAccessKeySeed, Long> {

    /**
     * 根据种子密钥ID查找
     */
    TempAccessKeySeed findBySeedKeyId(String seedKeyId);

    /**
     * 查找创建时间超过指定时间的最新一条种子记录（用于生成临时凭证）
     * 即创建时间早于 指定时间 的最新种子，确保种子已稳定可用
     */
    TempAccessKeySeed findFirstByCreateTimeLessThanOrderByIdDesc(java.time.LocalDateTime createTime);

    /**
     * 查找不在指定seedKeyId列表中的种子记录（用于清理不再使用的种子）
     */
    List<TempAccessKeySeed> findBySeedKeyIdNotIn(List<String> seedKeyIds);

    /**
     * 查询当前临时凭证表中仍在使用的所有种子ID（去重）
     */
    @Query("SELECT DISTINCT t.seedKeyId FROM TempAccessKey t")
    List<String> findDistinctSeedKeyIdsInUse();

}
