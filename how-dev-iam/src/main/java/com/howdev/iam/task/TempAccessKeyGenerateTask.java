package com.howdev.iam.task;

import com.howdev.iam.entity.TempAccessKey;
import com.howdev.iam.entity.TempAccessKeySeed;
import com.howdev.iam.handler.AlphanumericRandom;
import com.howdev.iam.handler.SimpleDesUtil;
import com.howdev.iam.repository.TempAccessKeyRepository;
import com.howdev.iam.repository.TempAccessKeySeedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 临时访问凭证生成任务
 *
 * <h3>功能说明</h3>
 * <p>从 t_temp_access_key_seed 表中取出一个已稳定的种子密钥，
 * 批量生成临时访问凭证（temp_access_key_id / temp_access_secret_key / temp_access_token），
 * 使用种子密钥加密后写入 t_temp_access_key 表。</p>
 *
 * <h3>生成规则</h3>
 * <ol>
 *   <li>从 t_temp_access_key_seed 表查询创建时间超过1小时的最新一条种子记录，获取 seed_key_id 和 seed_key</li>
 *   <li>循环1000次，每次：
 *     <ul>
 *       <li>生成96字节随机字符串</li>
 *       <li>前32字节作为 temp_access_token，中间32字节（末位固定为'a'）作为 temp_access_key_id，后32字节作为 temp_access_secret_key</li>
 *       <li>使用 seed_key 对 temp_access_token 和 temp_access_secret_key 进行 AES-128-CBC 加密</li>
 *       <li>将加密后的数据连同 temp_access_key_id、时间戳、分片标识(distribute_base)、seed_key_id 写入 t_temp_access_key 表</li>
 *     </ul>
 *   </li>
 * </ol>
 */
@Component
public class TempAccessKeyGenerateTask {

    private static final Logger logger = LoggerFactory.getLogger(TempAccessKeyGenerateTask.class);

    /**
     * 每批生成的凭证数量（循环1000次，distribute_base 为 0-999）
     */
    private static final int BATCH_SIZE = 1000;

    /**
     * 凭证有效期：70天（3600 * 24 * 70 秒）
     */
    private static final long EXPIRE_DURATION_SECONDS = 3600L * 24 * 70;

    /**
     * 种子密钥的最小稳定时间：1小时（查询创建时间超过1小时的种子）
     */
    private static final long SEED_STABLE_HOURS = 1;

    /**
     * 每条凭证中随机字符串的总长度（96字节）
     */
    private static final int RANDOM_STRING_TOTAL_LENGTH = 96;

    /**
     * temp_access_token 的长度（前32字节）
     */
    private static final int TOKEN_LENGTH = 32;

    /**
     * temp_access_key_id 的长度（中间32字节，末位固定为'a'，实际取31字节+1字节）
     */
    private static final int KEY_ID_LENGTH = 32;

    /**
     * temp_access_secret_key 的长度（后32字节）
     */
    private static final int SECRET_KEY_LENGTH = 32;

    @Resource
    private TempAccessKeySeedRepository tempAccessKeySeedRepository;

    @Resource
    private TempAccessKeyRepository tempAccessKeyRepository;

    /**
     * 执行临时访问凭证生成任务
     *
     * <p>核心流程：</p>
     * <ol>
     *   <li>查询创建时间超过1小时的最新种子密钥</li>
     *   <li>批量生成1000条临时凭证，distribute_base 从 0 到 999</li>
     *   <li>每条凭证的 token 和 secret_key 使用种子密钥加密后存储</li>
     * </ol>
     */
    public void execute() {
        logger.info("TempAccessKeyGenerateTask 开始执行");

        try {
            // 第1步：查询已稳定的种子密钥（创建时间超过1小时的最新一条）
            //LocalDateTime stableTime = LocalDateTime.now().minusHours(SEED_STABLE_HOURS);
            LocalDateTime stableTime = LocalDateTime.now().minusMinutes(SEED_STABLE_HOURS);
            TempAccessKeySeed seed = tempAccessKeySeedRepository.findFirstByCreateTimeLessThanOrderByIdDesc(stableTime);
            if (seed == null) {
                logger.error("TempAccessKeyGenerateTask 未找到可用的种子密钥（创建时间超过{}小时）", SEED_STABLE_HOURS);
                return;
            }

            String seedKeyId = seed.getSeedKeyId();
            String seedKey = seed.getSeedKey();
            logger.info("TempAccessKeyGenerateTask 使用种子密钥, seedKeyId={}", seedKeyId);

            // 第2步：计算凭证的创建时间和过期时间
            long createTimestamp = System.currentTimeMillis() / 1000;
            long expireTimestamp = createTimestamp + EXPIRE_DURATION_SECONDS;

            // 第3步：批量生成临时凭证
            for (int i = 0; i < BATCH_SIZE; i++) {
                // 生成96字节随机字符串
                String randString = AlphanumericRandom.generateFixedLengthRandomString(RANDOM_STRING_TOTAL_LENGTH);

                // 拆分随机字符串
                // 前32字节 → token（明文，加密后存储）
                String token = randString.substring(0, TOKEN_LENGTH);
                // 中间31字节 + 末位固定'a' → temp_access_key_id（明文存储）
                String tempAccessKeyId = randString.substring(TOKEN_LENGTH, TOKEN_LENGTH + KEY_ID_LENGTH - 1) + "a";
                // 后32字节 → secret_key（明文，加密后存储）
                String secretKey = randString.substring(TOKEN_LENGTH + KEY_ID_LENGTH - 1 + 1);

                // 使用种子密钥加密 token 和 secret_key
                String encryptedToken = SimpleDesUtil.encrypt(seedKey, token);
                String encryptedSecretKey = SimpleDesUtil.encrypt(seedKey, secretKey);

                // 构建实体并写入数据库
                TempAccessKey tempAccessKey = new TempAccessKey();
                tempAccessKey.setTempAccessKeyId(tempAccessKeyId);
                tempAccessKey.setTempAccessSecretKey(encryptedSecretKey);
                tempAccessKey.setTempAccessToken(encryptedToken);
                tempAccessKey.setKeyCreateTime(createTimestamp);
                tempAccessKey.setKeyExpireTime(expireTimestamp);
                tempAccessKey.setDistributeBase(500);
                tempAccessKey.setSeedKeyId(seedKeyId);
                tempAccessKey.setCreateTime(LocalDateTime.now());
                tempAccessKey.setUpdateTime(LocalDateTime.now());

                tempAccessKeyRepository.save(tempAccessKey);
            }

            logger.info("TempAccessKeyGenerateTask 执行成功, 共生成{}条临时凭证, seedKeyId={}", BATCH_SIZE, seedKeyId);
        } catch (Exception e) {
            logger.error("TempAccessKeyGenerateTask 执行失败", e);
        }
    }

}
