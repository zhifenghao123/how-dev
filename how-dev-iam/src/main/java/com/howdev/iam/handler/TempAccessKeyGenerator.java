package com.howdev.iam.handler;

import com.howdev.iam.bo.TempAccessKeyTokenContextBo;
import com.howdev.iam.dto.TempAccessKeyResp;
import com.howdev.iam.enumeration.RetCodeEnum;
import com.howdev.iam.exception.RetCodeException;
import com.howdev.iam.util.JacksonUtil;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

/**
 * 临时访问密钥生成器
 *
 * <p>提供临时密钥的生成、解析、加解密等通用工具方法，
 * 与 {@link AccessKeyGenerator} 对应，负责临时密钥体系的底层密码学操作。</p>
 *
 * <h3>职责划分</h3>
 * <ul>
 *   <li><b>生成</b>：{@link #generateToken} — 使用种子凭证生成最终的临时访问密钥</li>
 *   <li><b>解析</b>：{@link #parseSessionToken} — 从 sessionToken 中解密还原业务数据</li>
 *   <li><b>派生</b>：{@link #deriveSecretId}、{@link #deriveSecretKey} — 从 sha256Token 派生 secretId 和 secretKey</li>
 *   <li><b>工具</b>：URL 安全 Base64 编解码、MD5、SHA-256 等基础方法</li>
 * </ul>
 */
public class TempAccessKeyGenerator {

    /**
     * token 数据前缀标识（版本号）
     */
    public static final String TOKEN_DATA_PREFIX = "0001";

    /**
     * 临时密钥 SecretId 前缀
     */
    public static final String SECRET_ID_PREFIX = "AKID";

    // ==================== 生成相关 ====================

    /**
     * 使用种子凭证生成最终的临时访问密钥
     *
     * <h3>生成流程</h3>
     * <ol>
     *   <li>用 seedToken 的前16字节作为 IV，对 "0001" + strToken 进行 AES-256-CBC 加密</li>
     *   <li>拼接 outputToken = seedSecretId + MD5("0001" + strToken) + urlSafeBase64(加密结果)</li>
     *   <li>对 outputToken 做 SHA-256 → sha256Token</li>
     *   <li>用 sha256Token 派生 outputSecretId（AES-256-CBC 加密 seedSecretId）</li>
     *   <li>用 sha256Token 派生 outputSecretKey（HMAC-SHA256 签名 seedSecretKey）</li>
     *   <li>返回 sessionToken、tmpSecretId（"AKID" + outputSecretId）、tmpSecretKey</li>
     * </ol>
     *
     * @param seedToken     种子 Token（32字节，用作 AES-256-CBC 密钥）
     * @param seedSecretId  种子 SecretId（32字节，明文）
     * @param seedSecretKey 种子 SecretKey（32字节，明文）
     * @param strToken      业务数据（序列化后的 JSON）
     * @param expireTime    过期时间戳（秒）
     * @return 最终的临时访问密钥响应
     */
    public static TempAccessKeyResp generateToken(String seedToken, String seedSecretId, String seedSecretKey,
                                                   String strToken, long expireTime) {
        try {
            // 第1步：用 seedToken 对业务数据进行 AES-256-CBC 加密
            String tokenData = TOKEN_DATA_PREFIX + strToken;
            byte[] encryptedTokenData = aes256CbcEncrypt(seedToken, tokenData);

            // 第2步：拼接 outputToken = seedSecretId + MD5 + urlSafeBase64(加密结果)
            String md5Hex = md5Hex(tokenData);
            String encryptedTokenBase64 = urlSafeBase64Encode(encryptedTokenData);
            String outputToken = seedSecretId + md5Hex + encryptedTokenBase64;

            // 第3步：对 outputToken 做 SHA-256
            byte[] sha256Token = sha256(outputToken);

            // 第4步：派生 secretId
            String outputSecretId = deriveSecretId(sha256Token, seedSecretId);

            // 第5步：派生 secretKey
            String outputSecretKey = deriveSecretKey(sha256Token, seedSecretKey);

            // 第6步：构建返回结果
            TempAccessKeyResp resp = new TempAccessKeyResp();
            resp.setSessionToken(outputToken);
            resp.setAccessKeyId(SECRET_ID_PREFIX + outputSecretId);
            resp.setSecretAccessKey(outputSecretKey);
            resp.setExpireTime(expireTime);

            return resp;
        } catch (Exception e) {
            throw new RetCodeException(RetCodeEnum.FAILED, "生成临时密钥失败: " + e.getMessage());
        }
    }

    // ==================== 解析相关 ====================

    /**
     * 解析 sessionToken，解密还原业务数据并校验完整性
     *
     * <h3>解析流程</h3>
     * <ol>
     *   <li>从 sessionToken 中提取 seedSecretId(前32字节)、md5(32-64字节)、加密数据(64字节之后)</li>
     *   <li>用 seedToken 对加密数据进行 AES-256-CBC 解密</li>
     *   <li>校验 MD5 完整性</li>
     *   <li>校验版本号前缀 "0001"</li>
     *   <li>反序列化业务数据</li>
     * </ol>
     *
     * @param sessionToken 客户端传入的 sessionToken
     * @param seedToken    种子 Token（用于解密）
     * @return 解析后的业务上下文
     */
    public static TempAccessKeyTokenContextBo parseSessionToken(String sessionToken, String seedToken) {
        // 提取各部分
        String md5Part = sessionToken.substring(32, 64);
        String encryptedPart = sessionToken.substring(64);

        // 用 seedToken 解密
        String decryptedToken = aes256CbcDecrypt(seedToken, encryptedPart);

        // 校验 MD5 完整性
        String expectedMd5 = md5Hex(decryptedToken);
        if (!md5Part.equals(expectedMd5)) {
            throw new RetCodeException(RetCodeEnum.FAILED, "sessionToken 完整性校验失败");
        }

        // 校验版本号
        if (!decryptedToken.startsWith(TOKEN_DATA_PREFIX)) {
            throw new RetCodeException(RetCodeEnum.FAILED, "sessionToken 版本号校验失败");
        }

        // 反序列化业务数据
        String tokenContextJson = decryptedToken.substring(TOKEN_DATA_PREFIX.length());
        TempAccessKeyTokenContextBo tokenContext;
        try {
            tokenContext = JacksonUtil.fromJson(tokenContextJson, TempAccessKeyTokenContextBo.class);
        } catch (Exception e) {
            throw new RetCodeException(RetCodeEnum.FAILED, "sessionToken 业务数据解析失败: " + e.getMessage());
        }
        if (tokenContext == null) {
            throw new RetCodeException(RetCodeEnum.FAILED, "sessionToken 业务数据解析失败");
        }

        return tokenContext;
    }

    /**
     * 从 sessionToken 中提取 seedSecretId（前32字节）
     *
     * @param sessionToken 客户端传入的 sessionToken
     * @return seedSecretId
     */
    public static String extractSeedSecretId(String sessionToken) {
        if (sessionToken == null || sessionToken.length() < 65) {
            throw new RetCodeException(RetCodeEnum.ILLEGAL_ARGUMENT, "sessionToken 格式无效");
        }
        return sessionToken.substring(0, 32);
    }

    // ==================== 派生相关 ====================

    /**
     * 用 sha256Token 对 seedSecretId 进行 AES-256-CBC 加密，派生最终的 secretId
     *
     * @param sha256Token  sessionToken 的 SHA-256 哈希（32字节）
     * @param seedSecretId 种子 SecretId
     * @return urlSafeBase64 编码后的加密结果（不含 "AKID" 前缀）
     */
    public static String deriveSecretId(byte[] sha256Token, String seedSecretId) {
        try {
            byte[] iv = Arrays.copyOf(sha256Token, 16);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec aesKey = new SecretKeySpec(sha256Token, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec);
            byte[] encrypted = cipher.doFinal(seedSecretId.getBytes(StandardCharsets.UTF_8));
            return urlSafeBase64Encode(encrypted);
        } catch (Exception e) {
            throw new RetCodeException(RetCodeEnum.FAILED, "派生 secretId 失败: " + e.getMessage());
        }
    }

    /**
     * 用 sha256Token 对 seedSecretKey 进行 HMAC-SHA256 签名，派生最终的 secretKey
     *
     * @param sha256Token   sessionToken 的 SHA-256 哈希（32字节）
     * @param seedSecretKey 种子 SecretKey
     * @return Base64 编码后的 HMAC 结果
     */
    public static String deriveSecretKey(byte[] sha256Token, String seedSecretKey) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec hmacKey = new SecretKeySpec(sha256Token, "HmacSHA256");
            hmac.init(hmacKey);
            byte[] hmacResult = hmac.doFinal(seedSecretKey.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmacResult);
        } catch (Exception e) {
            throw new RetCodeException(RetCodeEnum.FAILED, "派生 secretKey 失败: " + e.getMessage());
        }
    }

    /**
     * 校验 secretId 是否与种子凭证匹配
     *
     * <p>用 sha256Token 对 seedSecretId 加密后，与传入的 secretId 进行比对。</p>
     *
     * @param secretId     客户端传入的 secretId（格式为 "AKID" + urlSafeBase64 加密串）
     * @param sha256Token  sessionToken 的 SHA-256 哈希
     * @param seedSecretId 种子 SecretId
     * @return 是否匹配
     */
    public static boolean verifySecretId(String secretId, byte[] sha256Token, String seedSecretId) {
        if (secretId == null || !secretId.startsWith(SECRET_ID_PREFIX)) {
            return false;
        }
        String secretIdPayload = secretId.substring(SECRET_ID_PREFIX.length());
        String expectedPayload = deriveSecretId(sha256Token, seedSecretId);
        return secretIdPayload.equals(expectedPayload);
    }

    // ==================== 基础加解密工具 ====================

    /**
     * AES-256-CBC 加密
     * <p>使用 key 的前16字节作为 IV</p>
     *
     * @param key       密钥字符串（32字节）
     * @param plaintext 明文
     * @return 加密后的字节数组
     */
    public static byte[] aes256CbcEncrypt(String key, String plaintext) {
        try {
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte[] iv = Arrays.copyOf(keyBytes, 16);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec aesKey = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec);
            return cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RetCodeException(RetCodeEnum.FAILED, "AES 加密失败: " + e.getMessage());
        }
    }

    /**
     * AES-256-CBC 解密
     * <p>使用 key 的前16字节作为 IV，密文为 urlSafeBase64 编码</p>
     *
     * @param key              密钥字符串（32字节）
     * @param urlSafeBase64Str urlSafeBase64 编码的密文
     * @return 解密后的明文
     */
    public static String aes256CbcDecrypt(String key, String urlSafeBase64Str) {
        try {
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte[] iv = Arrays.copyOf(keyBytes, 16);
            byte[] encryptedData = urlSafeBase64Decode(urlSafeBase64Str);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec aesKey = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec);
            byte[] decryptedBytes = cipher.doFinal(encryptedData);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RetCodeException(RetCodeEnum.FAILED, "AES 解密失败: " + e.getMessage());
        }
    }

    // ==================== 哈希工具 ====================

    /**
     * 计算 SHA-256 哈希
     *
     * @param input 输入字符串
     * @return 32字节的哈希结果
     */
    public static byte[] sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(input.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 计算失败", e);
        }
    }

    /**
     * 计算 MD5 摘要并返回32位小写十六进制字符串
     *
     * @param input 输入字符串
     * @return 32位小写十六进制 MD5 字符串
     */
    public static String md5Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 计算失败", e);
        }
    }

    // ==================== 编码工具 ====================

    /**
     * URL 安全的 Base64 编码
     * <p>将 '+' 替换为 '-'，'/' 替换为 '_'，去掉末尾的 '='</p>
     *
     * @param data 原始字节数组
     * @return URL 安全的 Base64 字符串
     */
    public static String urlSafeBase64Encode(byte[] data) {
        String base64 = Base64.getEncoder().encodeToString(data);
        return base64.replace('+', '-').replace('/', '_').replaceAll("=+$", "");
    }

    /**
     * URL 安全的 Base64 解码
     * <p>将 '-' 替换回 '+'，'_' 替换回 '/'，补齐末尾的 '='</p>
     *
     * @param data URL 安全的 Base64 字符串
     * @return 解码后的字节数组
     */
    public static byte[] urlSafeBase64Decode(String data) {
        String base64 = data.replace('-', '+').replace('_', '/');
        int mod4 = base64.length() % 4;
        if (mod4 > 0) {
            base64 += "====".substring(mod4);
        }
        return Base64.getDecoder().decode(base64);
    }
}
