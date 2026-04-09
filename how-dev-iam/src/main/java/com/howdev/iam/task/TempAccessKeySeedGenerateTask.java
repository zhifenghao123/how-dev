package com.howdev.iam.task;

import com.howdev.iam.entity.TempAccessKeySeed;
import com.howdev.iam.handler.AlphanumericRandom;
import com.howdev.iam.repository.TempAccessKeySeedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 种子密钥生成任务
 *
 * <h3>功能说明</h3>
 * <p>定期生成新的种子密钥（seed_key），写入 t_temp_access_key_seed 表。
 * 种子密钥用于对临时访问密钥的 secret_key 和 token 进行 AES 加密。</p>
 *
 * <h3>生成规则</h3>
 * <ul>
 *   <li>seed_key_id：32位随机字符串，用于唯一标识该种子</li>
 *   <li>seed_key：16字节随机可打印字符串，用于 AES-128-CBC 加密</li>
 * </ul>
 */
@Component
public class TempAccessKeySeedGenerateTask {

    private static final Logger logger = LoggerFactory.getLogger(TempAccessKeySeedGenerateTask.class);

    /**
     * 种子密钥长度（16字节）
     */
    private static final int SEED_KEY_LENGTH = 16;

    /**
     * 种子密钥ID长度（32字节）
     */
    private static final int SEED_KEY_ID_LENGTH = 32;

    @Resource
    private TempAccessKeySeedRepository tempAccessKeySeedRepository;

    /**
     * 执行种子密钥生成任务
     *
     * <p>生成一条新的种子密钥记录，包含：</p>
     * <ul>
     *   <li>seed_key_id：32位随机字符串，用于唯一标识该种子</li>
     *   <li>seed_key：16字节随机可打印字符串，用于 AES-128-CBC 加密</li>
     * </ul>
     */
    public void execute() {
        logger.info("TempAccessKeySeedGenerateTask 开始执行");

        try {
            // 生成种子密钥ID（32位随机字符串）
            String seedKeyId = AlphanumericRandom.generateFixedLengthRandomString(SEED_KEY_ID_LENGTH);

            // 生成种子密钥（16字节随机可打印字符串）
            String seedKey = AlphanumericRandom.generateFixedLengthRandomString(SEED_KEY_LENGTH);

            // 构建实体并写入数据库
            TempAccessKeySeed seed = new TempAccessKeySeed();
            seed.setSeedKeyId(seedKeyId);
            seed.setSeedKey(seedKey);
            seed.setCreateTime(LocalDateTime.now());
            seed.setUpdateTime(LocalDateTime.now());

            tempAccessKeySeedRepository.save(seed);

            logger.info("TempAccessKeySeedGenerateTask 执行成功, seedKeyId={}", seedKeyId);
        } catch (Exception e) {
            logger.error("TempAccessKeySeedGenerateTask 执行失败", e);
        }
    }

}
