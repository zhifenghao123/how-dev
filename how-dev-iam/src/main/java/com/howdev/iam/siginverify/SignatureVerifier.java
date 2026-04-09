package com.howdev.iam.siginverify;

import com.howdev.iam.bo.TempAccessKeyBo;
import com.howdev.iam.entity.UserAccessKey;
import com.howdev.iam.handler.AttributeEncryptDesUtil;
import com.howdev.iam.handler.SignatureUtil;
import com.howdev.iam.service.TempAccessKeyService;
import com.howdev.iam.service.UserAccessKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.ZoneId;
import java.util.Map;
import java.util.TreeMap;

/**
 * 签名验证服务实现
 *
 * <p>参考签名方法，实现服务端对 API 请求签名的验证。</p>
 *
 * <h3>验证流程</h3>
 * <ol>
 *   <li>从 Authorization 头中解析出 AccessKeyId、SignedHeaders、Signature</li>
 *   <li>根据 AccessKeyId 从数据库查询对应的 SecretAccessKey</li>
 *   <li>校验时间戳是否在允许的偏差范围内（防止重放攻击）</li>
 *   <li>按照签名方法的流程重新计算签名</li>
 *   <li>将计算出的签名与请求中携带的签名进行比对</li>
 * </ol>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SignatureVerifier {

    private final UserAccessKeyService userAccessKeyService;
    private final TempAccessKeyService tempAccessKeyService;

    /**
     * 临时密钥的 SessionToken 请求头名称
     */
    private static final String HEADER_SESSION_TOKEN = "X-Session-Token";

    /**
     * 密钥类型常量
     */
    private static final int KEY_TYPE_PERMANENT = 1;
    private static final int KEY_TYPE_TEMP = 2;

    public boolean verifySignature(HttpServletRequest request, String requestBody) {
        try {
            // ========== 第一步：提取并解析 Authorization 头 ==========
            String authorization = request.getHeader(SignatureUtil.HEADER_AUTHORIZATION);
            if (authorization == null || authorization.isEmpty()) {
                log.warn("签名验证失败：缺少 Authorization 请求头");
                return false;
            }

            Map<String, String> authParts = SignatureUtil.parseAuthorization(authorization);
            String accessKeyId = authParts.get("accessKeyId");
            String signedHeaderNames = authParts.get("signedHeaders");
            String requestSignature = authParts.get("signature");

            log.debug("解析 Authorization 头：accessKeyId={}, signedHeaders={}",
                    accessKeyId, signedHeaderNames);

            // ========== 第二步：判断密钥类型并获取 SecretAccessKey ==========
            String sessionToken = request.getHeader(HEADER_SESSION_TOKEN);
            int keyType = detectKeyType(sessionToken);
            String secretAccessKey;
            String userId = null;

            if (keyType == KEY_TYPE_PERMANENT) {
                // ---------- 持久密钥验签 ----------
                log.debug("检测到持久密钥, accessKeyId={}", accessKeyId);
                UserAccessKey userAccessKey = userAccessKeyService.queryAccessKeyByAccessKeyId(accessKeyId);
                if (userAccessKey == null) {
                    log.warn("签名验证失败：AccessKeyId 不存在, accessKeyId={}", accessKeyId);
                    return false;
                }

                // 检查密钥状态
                if (!"ENABLE".equals(userAccessKey.getStatus())) {
                    log.warn("签名验证失败：AccessKey 已被禁用, accessKeyId={}", accessKeyId);
                    return false;
                }

                // 解密获取明文 SecretAccessKey
                long createTimestamp = userAccessKey.getCreateTime()
                        .atZone(ZoneId.of("Asia/Shanghai"))
                        .toInstant()
                        .toEpochMilli();
                secretAccessKey = AttributeEncryptDesUtil.decrypt(
                        userAccessKey.getSecretAccessKey(),
                        userAccessKey.getUserId().hashCode(),
                        createTimestamp);
                userId = userAccessKey.getUserId();
            } else {
                // ---------- 临时密钥验签 ----------
                log.debug("检测到临时密钥, accessKeyId={}", accessKeyId);
                TempAccessKeyBo tempKeyBo;
                try {
                    tempKeyBo = tempAccessKeyService.queryTempAccessKey(accessKeyId, sessionToken);
                } catch (Exception e) {
                    log.warn("签名验证失败：临时密钥查询失败, accessKeyId={}, message={}", accessKeyId, e.getMessage());
                    return false;
                }
                secretAccessKey = tempKeyBo.getSecretAccessKey();
                userId = tempKeyBo.getTokenContext() != null ? tempKeyBo.getTokenContext().getUserId() : null;
            }

            // ========== 第三步：校验时间戳 ==========
            String timestampStr = request.getHeader("X-Timestamp");
            if (timestampStr == null || timestampStr.isEmpty()) {
                log.warn("签名验证失败：缺少 X-Timestamp 请求头");
                return false;
            }

            long requestTimestamp;
            try {
                requestTimestamp = Long.parseLong(timestampStr);
            } catch (NumberFormatException e) {
                log.warn("签名验证失败：X-Timestamp 格式不正确, value={}", timestampStr);
                return false;
            }

            long currentTimestamp = System.currentTimeMillis() / 1000;
            if (Math.abs(currentTimestamp - requestTimestamp) > SignatureUtil.MAX_TIMESTAMP_DIFF_SECONDS) {
                log.warn("签名验证失败：请求时间戳超出允许范围, requestTimestamp={}, currentTimestamp={}, diff={}s",
                        requestTimestamp, currentTimestamp, Math.abs(currentTimestamp - requestTimestamp));
                return false;
            }

            // ========== 第四步：重新构建规范请求串 ==========
            String httpMethod = request.getMethod().toUpperCase();
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();

            // 解析查询参数
            Map<String, String> queryParams = new TreeMap<>();
            if (queryString != null && !queryString.isEmpty()) {
                for (String param : queryString.split("&")) {
                    String[] kv = param.split("=", 2);
                    if (kv.length == 2) {
                        queryParams.put(kv[0], kv[1]);
                    }
                }
            }

            // 构建参与签名的请求头
            TreeMap<String, String> signedHeaders = new TreeMap<>();
            for (String headerName : signedHeaderNames.split(";")) {
                String headerValue = request.getHeader(headerName.trim());
                if (headerValue != null) {
                    signedHeaders.put(headerName.trim().toLowerCase(), headerValue.trim());
                }
            }

            // 计算请求体的 SHA-256 哈希
            String hashedPayload = SignatureUtil.sha256Hex(requestBody == null ? "" : requestBody);

            // 构建规范请求串
            String canonicalRequest = SignatureUtil.buildCanonicalRequest(
                    httpMethod, uri, queryParams, signedHeaders, hashedPayload);

            log.debug("规范请求串：\n{}", canonicalRequest);

            // ========== 第五步：构建待签名字符串 ==========
            String hashedCanonicalRequest = SignatureUtil.sha256Hex(canonicalRequest);
            String stringToSign = SignatureUtil.buildStringToSign(
                    requestTimestamp, accessKeyId, hashedCanonicalRequest);

            log.debug("待签名字符串：\n{}", stringToSign);

            // ========== 第六步：计算签名并比对 ==========

            // 计算期望的签名
            String expectedSignature = SignatureUtil.calculateSignature(
                    secretAccessKey, accessKeyId, stringToSign);

            log.debug("期望签名：{}", expectedSignature);
            log.debug("请求签名：{}", requestSignature);

            // 使用常量时间比较，防止时序攻击
            boolean result = constantTimeEquals(expectedSignature, requestSignature);
            if (!result) {
                log.warn("签名验证失败：签名不匹配, accessKeyId={}, keyType={}", accessKeyId, keyType == KEY_TYPE_TEMP ? "临时密钥" : "持久密钥");
            } else {
                log.info("签名验证成功, accessKeyId={}, userId={}, keyType={}", accessKeyId, userId, keyType == KEY_TYPE_TEMP ? "临时密钥" : "持久密钥");
            }

            return result;

        } catch (IllegalArgumentException e) {
            log.warn("签名验证失败：参数解析异常, message={}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("签名验证失败：内部异常", e);
            return false;
        }
    }

    /**
     * 检测密钥类型
     *
     * <p>根据请求中是否携带 SessionToken 来判断密钥类型：</p>
     * <ul>
     *   <li>无 SessionToken → 持久密钥（type=1）</li>
     *   <li>有 SessionToken → 临时密钥（type=2）</li>
     * </ul>
     *
     * @param sessionToken 请求中的 SessionToken（可能为 null）
     * @return 密钥类型：1=持久密钥，2=临时密钥
     */
    private int detectKeyType(String sessionToken) {
        if (sessionToken == null || sessionToken.isEmpty()) {
            return KEY_TYPE_PERMANENT;
        }
        return KEY_TYPE_TEMP;
    }

    /**
     * 常量时间字符串比较
     *
     * <p>防止时序攻击（Timing Attack）：无论两个字符串在哪个位置不同，
     * 比较所花费的时间都是相同的，攻击者无法通过测量响应时间来推断签名内容。</p>
     *
     * @param a 字符串 a
     * @param b 字符串 b
     * @return 两个字符串相等返回 true
     */
    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        if (a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
