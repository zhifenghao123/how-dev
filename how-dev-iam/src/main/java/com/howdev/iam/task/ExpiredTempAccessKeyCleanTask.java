package com.howdev.iam.task;

import com.howdev.iam.entity.TempAccessKey;
import com.howdev.iam.entity.TempAccessKeySeed;
import com.howdev.iam.repository.TempAccessKeyRepository;
import com.howdev.iam.repository.TempAccessKeySeedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 过期临时凭证清理任务
 *
 * <h3>功能说明</h3>
 * <p>定期清理过期的临时访问凭证和不再被引用的种子密钥。</p>
 *
 * <h3>清理规则</h3>
 * <ol>
 *   <li>删除 t_temp_access_key 表中 key_expire_time 小于 (当前时间 - 48小时) 的记录（清理过期凭证，保留48小时缓冲）</li>
 *   <li>查询 t_temp_access_key 表中仍在使用的所有 seed_key_id 集合</li>
 *   <li>删除 t_temp_access_key_seed 表中不再被任何凭证引用的种子密钥</li>
 * </ol>
 */
@Component
public class ExpiredTempAccessKeyCleanTask {

    private static final Logger logger = LoggerFactory.getLogger(ExpiredTempAccessKeyCleanTask.class);

    /**
     * 过期凭证的缓冲时间：48小时（172800秒）
     */
    private static final long EXPIRE_BUFFER_SECONDS = 172800L;

    @Resource
    private TempAccessKeyRepository tempAccessKeyRepository;

    @Resource
    private TempAccessKeySeedRepository tempAccessKeySeedRepository;

    /**
     * 执行过期凭证清理任务
     *
     * <p>核心流程：</p>
     * <ol>
     *   <li>删除 t_temp_access_key 表中过期超过48小时的凭证</li>
     *   <li>查询 t_temp_access_key 表中仍在使用的所有 seed_key_id</li>
     *   <li>删除 t_temp_access_key_seed 表中不再被任何凭证引用的种子密钥</li>
     * </ol>
     */
    @Transactional
    public void execute() {
        logger.info("ExpiredTempAccessKeyCleanTask 开始执行");

        try {
            // 第1步：清理过期的临时凭证（过期时间 + 48小时缓冲）
            long expireThreshold = System.currentTimeMillis() / 1000 - EXPIRE_BUFFER_SECONDS;
            List<TempAccessKey> expiredKeys = tempAccessKeyRepository.findByKeyExpireTimeLessThan(expireThreshold);
            if (!expiredKeys.isEmpty()) {
                tempAccessKeyRepository.deleteAll(expiredKeys);
                logger.info("ExpiredTempAccessKeyCleanTask 清理过期凭证{}条, 过期阈值时间戳={}", expiredKeys.size(), expireThreshold);
            } else {
                logger.info("ExpiredTempAccessKeyCleanTask 无过期凭证需要清理");
            }

            // 第2步：查询仍在使用的种子ID集合
            List<String> seedKeyIdsInUse = tempAccessKeySeedRepository.findDistinctSeedKeyIdsInUse();

            // 第3步：清理不再被引用的种子密钥
            if (seedKeyIdsInUse.isEmpty()) {
                // 如果凭证表已清空，则清理所有种子密钥
                long seedCount = tempAccessKeySeedRepository.count();
                if (seedCount > 0) {
                    tempAccessKeySeedRepository.deleteAll();
                    logger.info("ExpiredTempAccessKeyCleanTask 凭证表已清空, 清理全部种子密钥{}条", seedCount);
                }
            } else {
                List<TempAccessKeySeed> unusedSeeds = tempAccessKeySeedRepository.findBySeedKeyIdNotIn(seedKeyIdsInUse);
                if (!unusedSeeds.isEmpty()) {
                    List<String> unusedSeedKeyIds = unusedSeeds.stream()
                            .map(TempAccessKeySeed::getSeedKeyId)
                            .collect(Collectors.toList());
                    tempAccessKeySeedRepository.deleteAll(unusedSeeds);
                    logger.info("ExpiredTempAccessKeyCleanTask 清理不再使用的种子密钥{}条, seedKeyIds={}", unusedSeeds.size(), unusedSeedKeyIds);
                } else {
                    logger.info("ExpiredTempAccessKeyCleanTask 无不再使用的种子密钥需要清理");
                }
            }

            logger.info("ExpiredTempAccessKeyCleanTask 执行完成");
        } catch (Exception e) {
            logger.error("ExpiredTempAccessKeyCleanTask 执行失败", e);
        }
    }

    public static void main(String[] args) {
        new ExpiredTempAccessKeyCleanTask().execute();
        System.exit(0);
    }

}
