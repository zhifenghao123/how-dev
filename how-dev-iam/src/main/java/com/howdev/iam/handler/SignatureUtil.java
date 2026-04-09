package com.howdev.iam.handler;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 签名方法工具类
 *
 * <p>参考签名方法实现，用于 API 请求的签名生成与验证。</p>
 *
 * <h3>签名流程概览</h3>
 * <p>签名过程分为三步：</p>
 * <ol>
 *   <li><b>拼接规范请求串（CanonicalRequest）</b>：将 HTTP 方法、URI、查询参数、请求头、请求体等按规范格式拼接</li>
 *   <li><b>拼接待签名字符串（StringToSign）</b>：将签名算法、时间戳、AccessKeyId 和规范请求串的哈希值拼接</li>
 *   <li><b>计算签名（Signature）</b>：使用派生密钥（通过 AccessKeyId 进行 HMAC-SHA256 派生）对待签名字符串进行签名</li>
 * </ol>
 *
 * <h3>Authorization 头格式</h3>
 * <pre>
 * HMAC-SHA256 AccessKeyId={AccessKeyId},
 *     SignedHeaders={SignedHeaders}, Signature={Signature}
 * </pre>
 *
 * <h3>签名计算示例</h3>
 * <pre>
 * 假设：
 *   AccessKeyId = "AKIDxxx"
 *   SecretAccessKey = "yyy"
 *   Timestamp = 1551113065
 *   HTTP Method = "POST"
 *   URI = "/user/accessKey/list"
 *   Body = "{\"userId\":\"test-user\"}"
 *
 * 第一步 - 规范请求串：
 *   CanonicalRequest =
 *     POST\n
 *     /user/accessKey/list\n
 *     \n
 *     content-type:application/json; charset=utf-8\n
 *     host:localhost:8080\n
 *     \n
 *     content-type;host\n
 *     SHA256(body)
 *
 * 第二步 - 待签名字符串：
 *   StringToSign =
 *     HMAC-SHA256\n
 *     1551113065\n
 *     AKIDxxx\n
 *     SHA256(CanonicalRequest)
 *
 * 第三步 - 计算签名：
 *   SecretSigning = HMAC_SHA256(SecretAccessKey, AccessKeyId)
 *   Signature = HexEncode(HMAC_SHA256(SecretSigning, StringToSign))
 * </pre>
 *
 * @author haozhifeng
 */
public class SignatureUtil {

    /** 签名算法标识 */
    public static final String ALGORITHM = "HMAC-SHA256";

    public static final String HEADER_AUTHORIZATION = "Authorization";



    /**
     * 签名允许的最大时间偏差（秒），超过此范围的请求将被视为过期
     * <p>默认 300 秒（5 分钟）</p>
     */
    public static final long MAX_TIMESTAMP_DIFF_SECONDS = 300;

    // ======================== 第一步：拼接规范请求串 ========================

    /**
     * 构建规范请求串（Canonical Request）
     *
     * <p>规范请求串的格式如下：</p>
     * <pre>
     * HTTPRequestMethod\n          -- HTTP 方法（大写），如 POST、GET
     * CanonicalURI\n               -- URI 路径，如 /user/accessKey/list
     * CanonicalQueryString\n       -- 查询参数，按 key 字典序排列，key=value 用 &amp; 连接
     * CanonicalHeaders\n           -- 参与签名的请求头，key 小写 + ":" + value（去首尾空格）+ "\n"
     * SignedHeaders\n              -- 参与签名的请求头名称列表，小写，分号分隔
     * HashedRequestPayload         -- 请求体的 SHA-256 哈希值（十六进制小写）
     * </pre>
     *
     * @param httpMethod          HTTP 方法（如 "POST"、"GET"）
     * @param uri                 请求 URI 路径（如 "/user/accessKey/list"）
     * @param queryParams         查询参数 Map（可为 null 或空）
     * @param signedHeaders       参与签名的请求头 Map（key 为小写，value 已去首尾空格）
     * @param hashedRequestPayload 请求体的 SHA-256 哈希值
     * @return 规范请求串
     */
    public static String buildCanonicalRequest(String httpMethod,
                                                String uri,
                                                Map<String, String> queryParams,
                                                TreeMap<String, String> signedHeaders,
                                                String hashedRequestPayload) {
        // 1. HTTP 方法（大写）
        String canonicalMethod = httpMethod.toUpperCase();

        // 2. URI 路径（不进行 URL 编码，保持原样）
        String canonicalUri = uri.isEmpty() ? "/" : uri;

        // 3. 查询参数：按 key 字典序排列，key=URLEncode(value) 用 & 连接
        String canonicalQueryString = buildCanonicalQueryString(queryParams);

        // 4. 请求头：按 key 字典序排列，格式为 "key:value\n"
        StringBuilder canonicalHeadersBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : signedHeaders.entrySet()) {
            canonicalHeadersBuilder.append(entry.getKey().toLowerCase())
                    .append(":")
                    .append(entry.getValue().trim())
                    .append("\n");
        }
        String canonicalHeaders = canonicalHeadersBuilder.toString();

        // 5. 参与签名的请求头名称列表，小写，分号分隔
        String signedHeaderKeys = signedHeaders.keySet().stream()
                .map(String::toLowerCase)
                .collect(Collectors.joining(";"));

        // 6. 拼接规范请求串
        return canonicalMethod + "\n"
                + canonicalUri + "\n"
                + canonicalQueryString + "\n"
                + canonicalHeaders + "\n"
                + signedHeaderKeys + "\n"
                + hashedRequestPayload;
    }

    /**
     * 构建规范查询字符串
     *
     * <p>将查询参数按 key 字典序排列，格式为 key=value，多个参数用 &amp; 连接。
     * 如果没有查询参数，返回空字符串。</p>
     *
     * @param queryParams 查询参数 Map
     * @return 规范查询字符串
     */
    public static String buildCanonicalQueryString(Map<String, String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return "";
        }
        return queryParams.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
    }

    // ======================== 第二步：拼接待签名字符串 ========================

    /**
     * 构建待签名字符串（String to Sign）
     *
     * <p>待签名字符串的格式如下：</p>
     * <pre>
     * Algorithm\n                  -- 签名算法，固定为 "HMAC-SHA256"
     * RequestTimestamp\n           -- 请求时间戳（Unix 秒级时间戳）
     * AccessKeyId\n                -- 访问密钥ID，用于标识请求方身份
     * HashedCanonicalRequest       -- 规范请求串的 SHA-256 哈希值
     * </pre>
     *
     * @param timestamp              请求时间戳（Unix 秒级）
     * @param accessKeyId            访问密钥ID
     * @param hashedCanonicalRequest 规范请求串的 SHA-256 哈希值
     * @return 待签名字符串
     */
    public static String buildStringToSign(long timestamp,
                                            String accessKeyId,
                                            String hashedCanonicalRequest) {
        return ALGORITHM + "\n"
                + timestamp + "\n"
                + accessKeyId + "\n"
                + hashedCanonicalRequest;
    }

    // ======================== 第三步：计算签名 ========================

    /**
     * 计算最终签名
     *
     * <p>签名计算采用 HMAC-SHA256 派生密钥的方式：</p>
     * <ol>
     *   <li>SecretSigning = HMAC_SHA256(SecretAccessKey, AccessKeyId)</li>
     *   <li>Signature = HexEncode(HMAC_SHA256(SecretSigning, StringToSign))</li>
     * </ol>
     *
     * <p>通过 AccessKeyId 派生签名密钥，使得每个密钥对拥有独立的签名密钥，
     * 即使 SecretAccessKey 相同，不同的 AccessKeyId 也会产生不同的签名。</p>
     *
     * @param secretAccessKey 用户的 SecretAccessKey
     * @param accessKeyId     用户的 AccessKeyId
     * @param stringToSign    待签名字符串
     * @return 十六进制编码的签名字符串
     * @throws Exception HMAC 计算异常
     */
    public static String calculateSignature(String secretAccessKey,
                                             String accessKeyId,
                                             String stringToSign) throws Exception {
        // 使用 AccessKeyId 派生签名密钥
        byte[] secretSigning = hmacSha256((secretAccessKey).getBytes(StandardCharsets.UTF_8), accessKeyId);

        // 使用派生密钥对待签名字符串进行签名
        byte[] signatureBytes = hmacSha256(secretSigning, stringToSign);
        return bytesToHex(signatureBytes);
    }

    // ======================== 构建 Authorization 头 ========================

    /**
     * 构建完整的 Authorization 请求头
     *
     * <p>格式为：</p>
     * <pre>
     * HMAC-SHA256 AccessKeyId={AccessKeyId}, SignedHeaders={SignedHeaders}, Signature={Signature}
     * </pre>
     *
     * @param accessKeyId   用户的 AccessKeyId
     * @param signedHeaders 参与签名的请求头名称（小写，分号分隔）
     * @param signature     签名值
     * @return Authorization 头的完整值
     */
    public static String buildAuthorization(String accessKeyId,
                                             String signedHeaders,
                                             String signature) {
        return ALGORITHM + " "
                + "AccessKeyId=" + accessKeyId + ", "
                + "SignedHeaders=" + signedHeaders + ", "
                + "Signature=" + signature;
    }

    // ======================== 解析 Authorization 头 ========================

    /**
     * 解析 Authorization 请求头
     *
     * <p>从 Authorization 头中提取 AccessKeyId、SignedHeaders 和 Signature。</p>
     *
     * <p>Authorization 头格式示例：</p>
     * <pre>
     * HMAC-SHA256 AccessKeyId=AKIDxxx,
     *     SignedHeaders=content-type;host, Signature=sss
     * </pre>
     *
     * @param authorization Authorization 头的完整值
     * @return 包含解析结果的 Map，key 为 accessKeyId、signedHeaders、signature
     * @throws IllegalArgumentException 格式不正确时抛出
     */
    public static Map<String, String> parseAuthorization(String authorization) {
        if (authorization == null || !authorization.startsWith(ALGORITHM + " ")) {
            throw new IllegalArgumentException("Authorization 头格式不正确，必须以 '" + ALGORITHM + " ' 开头");
        }

        // 去掉算法前缀
        String content = authorization.substring((ALGORITHM + " ").length()).trim();

        Map<String, String> result = new HashMap<>();

        // 解析各字段：AccessKeyId=xxx, SignedHeaders=xxx, Signature=xxx
        String[] parts = content.split(",\\s*");
        for (String part : parts) {
            String[] kv = part.split("=", 2);
            if (kv.length != 2) {
                throw new IllegalArgumentException("Authorization 头字段格式不正确: " + part);
            }
            String key = kv[0].trim();
            String value = kv[1].trim();

            switch (key) {
                case "AccessKeyId":
                    result.put("accessKeyId", value);
                    break;
                case "SignedHeaders":
                    result.put("signedHeaders", value);
                    break;
                case "Signature":
                    result.put("signature", value);
                    break;
                default:
                    break;
            }
        }

        // 校验必要字段
        if (!result.containsKey("accessKeyId")
                || !result.containsKey("signedHeaders") || !result.containsKey("signature")) {
            throw new IllegalArgumentException("Authorization 头缺少必要字段");
        }

        return result;
    }

    // ======================== 工具方法 ========================

    /**
     * HMAC-SHA256 计算
     *
     * @param key  密钥（字节数组）
     * @param data 待签名数据（字符串）
     * @return HMAC-SHA256 计算结果（字节数组）
     * @throws Exception 计算异常
     */
    public static byte[] hmacSha256(byte[] key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA256");
        mac.init(secretKeySpec);
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * SHA-256 哈希计算
     *
     * @param input 输入字符串
     * @return 十六进制编码的 SHA-256 哈希值（小写）
     */
    public static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 计算失败", e);
        }
    }

    /**
     * 字节数组转十六进制字符串（小写）
     *
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


}
