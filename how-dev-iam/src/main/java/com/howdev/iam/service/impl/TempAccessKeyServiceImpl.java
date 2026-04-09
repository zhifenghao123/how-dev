package com.howdev.iam.service.impl;

import com.howdev.iam.bo.TempAccessKeyBo;
import com.howdev.iam.bo.TempAccessKeySeedBo;
import com.howdev.iam.bo.TempAccessKeyTokenContextBo;
import com.howdev.iam.bo.PermissionBo;
import com.howdev.iam.dto.TempAccessKeyResp;
import com.howdev.iam.entity.TempAccessKey;
import com.howdev.iam.entity.TempAccessKeySeed;
import com.howdev.iam.enumeration.RetCodeEnum;
import com.howdev.iam.exception.RetCodeException;
import com.howdev.iam.handler.SimpleDesUtil;
import com.howdev.iam.handler.TempAccessKeyGenerator;
import com.howdev.iam.repository.TempAccessKeyRepository;
import com.howdev.iam.repository.TempAccessKeySeedRepository;
import com.howdev.iam.service.TempAccessKeyService;
import com.howdev.iam.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 临时访问密钥服务实现
 *
 * <h3>功能说明</h3>
 * <p>根据用户标识（userId）和期望过期时间，从预生成的临时凭证池中分配一组临时访问密钥。</p>
 *
 * <h3>核心流程</h3>
 * <ol>
 *   <li>通过 userId % 1000 计算分片标识（distribute_base），实现负载均衡</li>
 *   <li>从 t_temp_access_key 表中查询满足条件的凭证：
 *       distribute_base 匹配、过期时间充裕（> expireTime + 7200s）、创建时间已稳定（< 当前时间 - 5400s）</li>
 *   <li>通过凭证关联的 seed_key_id 查询种子密钥，解密得到种子凭证（seedToken, seedSecretId, seedSecretKey）</li>
 *   <li>使用种子凭证通过 {@link TempAccessKeyGenerator#generateToken} 生成最终的临时访问密钥</li>
 * </ol>
 */
@Service
public class TempAccessKeyServiceImpl implements TempAccessKeyService {

    private static final Logger logger = LoggerFactory.getLogger(TempAccessKeyServiceImpl.class);

    /**
     * 过期时间的安全余量：7200秒（2小时）
     * 确保分配的凭证在用户期望的过期时间之后仍有足够的有效期
     */
    private static final long EXPIRE_MARGIN_SECONDS = 7200L;

    /**
     * 凭证创建后的最小稳定时间：5400秒（1.5小时）
     * 确保分配的凭证已经稳定可用，避免分配刚刚生成的凭证
     */
    private static final long CREATE_STABLE_SECONDS = 5400L;

    @Resource
    private TempAccessKeyRepository tempAccessKeyRepository;

    @Resource
    private TempAccessKeySeedRepository tempAccessKeySeedRepository;

    @Override
    public TempAccessKeyResp applyTempAccessKey(String userId, long durationSeconds) {
        long currentTime = System.currentTimeMillis() / 1000;
        long expireTime = currentTime + durationSeconds;

        int userIdHash = userId.hashCode();

        // 第1步：获取种子凭证
        TempAccessKeySeedBo seedBo = applyTempAccessKeySeed(userIdHash, expireTime);

        // 第2步：构建业务数据 strToken
        TempAccessKeyTokenContextBo tokenContextBo = new TempAccessKeyTokenContextBo();
        tokenContextBo.setUserId(userId);
        tokenContextBo.setUserType("1");
        tokenContextBo.setExpireTime(seedBo.getExpireTime());
        List<PermissionBo> permissions = new ArrayList<>();
        PermissionBo permissionBo = new PermissionBo();
        permissionBo.setName("权限1");
        permissions.add(permissionBo);
        tokenContextBo.setPermissions(permissions);

        String strToken = JacksonUtil.toJson(tokenContextBo);

        // 第3步：使用种子凭证生成最终的临时访问密钥
        TempAccessKeyResp resp = TempAccessKeyGenerator.generateToken(
                seedBo.getSessionToken(),
                seedBo.getAccessKeyId(),
                seedBo.getSecretAccessKey(),
                strToken,
                expireTime
        );

        logger.info("applyTempAccessKey 成功, userId={}, accessKeyId={}, expireTime={}",
                userId, resp.getAccessKeyId(), expireTime);
        return resp;
    }

    /**
     * 申请种子凭证
     *
     * <p>从预生成的临时凭证池中，根据 userId 的分片标识查询一条满足条件的凭证，
     * 并使用种子密钥解密得到明文的 seedToken、seedSecretId、seedSecretKey。</p>
     *
     * @param userId     用户标识
     * @param expireTime 期望的过期时间戳（秒）
     * @return 种子凭证信息
     */
    public TempAccessKeySeedBo applyTempAccessKeySeed(long userId, long expireTime) {
        long currentTime = System.currentTimeMillis() / 1000;

        // 参数校验：过期时间不能早于当前时间
        if (expireTime < currentTime) {
            logger.error("applyTempAccessKeySeed 参数错误: userId={}, expireTime={} 早于当前时间={}", userId, expireTime, currentTime);
            throw new RetCodeException(RetCodeEnum.ILLEGAL_ARGUMENT, "expireTime 不能早于当前时间");
        }

        // 第1步：通过 userId 计算分片标识
        if (userId < 0) {
            userId = -userId;
        }
        int distributeBase = (int) (userId % 1000);

        // 第2步：查询满足条件的临时凭证
        // 条件：distribute_base 匹配、过期时间充裕、创建时间已稳定
        long minExpireTime = expireTime + EXPIRE_MARGIN_SECONDS;
        long maxCreateTime = currentTime - CREATE_STABLE_SECONDS;
        TempAccessKey tempAccessKey = tempAccessKeyRepository
                .findFirstByDistributeBaseAndKeyExpireTimeGreaterThanAndKeyCreateTimeLessThan(
                        distributeBase, minExpireTime, maxCreateTime);

        if (tempAccessKey == null) {
            logger.error("applyTempAccessKeySeed 未找到可用的临时凭证, userId={}, distributeBase={}", userId, distributeBase);
            throw new RetCodeException(RetCodeEnum.FAILED, "未找到可用的临时凭证");
        }

        // 第3步：解密种子凭证
        TempAccessKeySeedBo seedBo = decryptSeedCredentials(tempAccessKey);

        logger.info("applyTempAccessKeySeed 成功, userId={}, seedSecretId={}, expireTime={}",
                userId, seedBo.getAccessKeyId(), tempAccessKey.getKeyExpireTime());
        return seedBo;
    }

    /**
     * 根据 sessionToken 和 secretId 查询并验证临时访问密钥
     *
     * <h3>功能说明</h3>
     * <p>在验签/鉴权场景中，客户端携带 sessionToken 和 secretId 发起请求，
     * 服务端需要：解密 sessionToken 还原业务数据、校验完整性、反查 secretKey 用于签名验证。</p>
     *
     * <h3>核心流程</h3>
     * <ol>
     *   <li>从 sessionToken 提取 seedSecretId，查询种子凭证</li>
     *   <li>解密并校验 sessionToken（MD5完整性、版本号、业务数据反序列化）</li>
     *   <li>校验 secretId 与种子凭证的匹配关系</li>
     *   <li>派生最终的 secretKey</li>
     *   <li>校验过期时间，返回结果</li>
     * </ol>
     *
     * @param secretId     客户端传入的 secretId（格式为 "AKID" + urlSafeBase64 加密串）
     * @param sessionToken 客户端传入的 sessionToken
     * @return 临时密钥信息，包含还原的 secretKey 和业务上下文
     */
    @Override
    public TempAccessKeyBo queryTempAccessKey(String secretId, String sessionToken) {
        long currentTime = System.currentTimeMillis() / 1000;

        // 第1步：从 sessionToken 提取 seedSecretId，查询种子凭证
        String seedSecretId = TempAccessKeyGenerator.extractSeedSecretId(sessionToken);
        TempAccessKeySeedBo seedBo = queryTempAccessKeySeed(seedSecretId);
        String seedToken = seedBo.getSessionToken();
        String seedSecretKey = seedBo.getSecretAccessKey();

        try {
            // 第2步：解密并校验 sessionToken，还原业务数据
            TempAccessKeyTokenContextBo tokenContext = TempAccessKeyGenerator.parseSessionToken(sessionToken, seedToken);

            // 第3步：计算 sha256Token，用于后续的 secretId 校验和 secretKey 派生
            byte[] sha256Token = TempAccessKeyGenerator.sha256(sessionToken);

            // 第4步：校验 secretId
            if (!TempAccessKeyGenerator.verifySecretId(secretId, sha256Token, seedSecretId)) {
                logger.error("queryTempAccessKey secretId 校验失败, secretId={}", secretId);
                throw new RetCodeException(RetCodeEnum.FAILED, "secretId 校验失败");
            }

            // 第5步：派生最终的 secretKey
            String  outputSecretKey = TempAccessKeyGenerator.deriveSecretKey(sha256Token, seedSecretKey);

            // 第6步：校验过期时间
            if (currentTime > tokenContext.getExpireTime()) {
                logger.error("queryTempAccessKey 临时密钥已过期, expireTime={}, currentTime={}", tokenContext.getExpireTime(), currentTime);
                throw new RetCodeException(RetCodeEnum.FAILED, "临时密钥已过期");
            }

            // 第7步：构建返回结果
            TempAccessKeyBo result = new TempAccessKeyBo();
            result.setAccessKeyId(secretId);
            result.setSecretAccessKey(outputSecretKey);
            result.setTokenContext(tokenContext);
            result.setExpireTime(tokenContext.getExpireTime());

            logger.info("queryTempAccessKey 成功, secretId={}, userId={}, expireTime={}",
                    secretId, tokenContext.getUserId(), tokenContext.getExpireTime());
            return result;
        } catch (RetCodeException e) {
            throw e;
        } catch (Exception e) {
            logger.error("queryTempAccessKey 处理失败, secretId={}", secretId, e);
            throw new RetCodeException(RetCodeEnum.FAILED, "查询临时密钥失败: " + e.getMessage());
        }
    }

    /**
     * 根据 tempAccessKeyId 查询种子凭证
     *
     * <p>从 t_temp_access_key 表查询凭证记录，再通过 seedKeyId 查询种子密钥，
     * 使用种子密钥解密得到明文的 seedToken 和 seedSecretKey。</p>
     *
     * @param tempAccessKeyId 临时凭证 ID
     * @return 种子凭证信息
     */
    public TempAccessKeySeedBo queryTempAccessKeySeed(String tempAccessKeyId) {
        // 第1步：根据 tempAccessKeyId 查询临时凭证记录
        TempAccessKey tempAccessKey = tempAccessKeyRepository.findByTempAccessKeyId(tempAccessKeyId);
        if (tempAccessKey == null) {
            logger.error("queryTempAccessKeySeed 未找到临时凭证, tempAccessKeyId={}", tempAccessKeyId);
            throw new RetCodeException(RetCodeEnum.FAILED, "临时凭证不存在");
        }

        // 第2步：解密种子凭证
        return decryptSeedCredentials(tempAccessKey);
    }

    /**
     * 解密种子凭证
     *
     * <p>根据临时凭证记录中的 seedKeyId 查询种子密钥，
     * 使用种子密钥解密 tempAccessToken 和 tempAccessSecretKey。</p>
     *
     * @param tempAccessKey 临时凭证记录
     * @return 解密后的种子凭证信息
     */
    private TempAccessKeySeedBo decryptSeedCredentials(TempAccessKey tempAccessKey) {
        // 查询种子密钥
        String seedKeyId = tempAccessKey.getSeedKeyId();
        TempAccessKeySeed seed = tempAccessKeySeedRepository.findBySeedKeyId(seedKeyId);
        if (seed == null) {
            logger.error("decryptSeedCredentials 未找到种子密钥, seedKeyId={}", seedKeyId);
            throw new RetCodeException(RetCodeEnum.FAILED, "种子密钥不存在");
        }

        String seedKey = seed.getSeedKey();

        // 解密 token
        String seedToken;
        try {
            seedToken = SimpleDesUtil.decrypt(seedKey, tempAccessKey.getTempAccessToken());
        } catch (Exception e) {
            logger.error("decryptSeedCredentials 解密 token 失败, seedKeyId={}", seedKeyId, e);
            throw new RetCodeException(RetCodeEnum.FAILED, "解密 token 失败");
        }

        // 解密 secretKey
        String seedSecretKey;
        try {
            seedSecretKey = SimpleDesUtil.decrypt(seedKey, tempAccessKey.getTempAccessSecretKey());
        } catch (Exception e) {
            logger.error("decryptSeedCredentials 解密 secretKey 失败, seedKeyId={}", seedKeyId, e);
            throw new RetCodeException(RetCodeEnum.FAILED, "解密 secretKey 失败");
        }

        // 构建返回结果
        TempAccessKeySeedBo seedBo = new TempAccessKeySeedBo();
        seedBo.setAccessKeyId(tempAccessKey.getTempAccessKeyId());
        seedBo.setSecretAccessKey(seedSecretKey);
        seedBo.setSessionToken(seedToken);
        seedBo.setExpireTime(tempAccessKey.getKeyExpireTime());

        return seedBo;
    }
}
