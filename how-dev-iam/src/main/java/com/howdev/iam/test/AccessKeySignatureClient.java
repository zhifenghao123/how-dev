package com.howdev.iam.test;

import com.howdev.iam.handler.SignatureUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

/**
 * 签名方法客户端测试类
 *
 * <p>模拟客户端按照签名方法的规范构造签名并发送请求，用于验证服务端签名验证逻辑的正确性。</p>
 *
 * <h3>使用前提</h3>
 * <ol>
 *   <li>服务端已启动（默认 http://localhost:8080）</li>
 *   <li>已通过 /user/accessKey/apply 接口申请了 AccessKeyId 和 SecretAccessKey</li>
 *   <li>将申请到的密钥填入下方的 ACCESS_KEY_ID 和 SECRET_ACCESS_KEY 常量</li>
 * </ol>
 *
 * <h3>签名流程</h3>
 * <pre>
 * 1. 确定请求参数（HTTP方法、URI、Host、请求体等）
 * 2. 构建规范请求串（CanonicalRequest）
 * 3. 构建待签名字符串（StringToSign，包含 AccessKeyId）
 * 4. 使用 SecretAccessKey 和 AccessKeyId 派生密钥并计算签名
 * 5. 组装 Authorization 头
 * 6. 发送 HTTP 请求
 * </pre>
 */
public class AccessKeySignatureClient {

    // ==================== 配置区域（请替换为实际值） ====================

    /** 服务端地址 */
    private static final String BASE_URL = "http://localhost:8080";

    /** 请求的 Host（需要与实际请求的 Host 一致，参与签名计算） */
    private static final String HOST = "localhost:8080";

    /**
     * AccessKeyId - 通过 /user/accessKey/apply 接口申请获得
     * <p>请替换为实际的 AccessKeyId</p>
     */
    private static final String ACCESS_KEY_ID = "";

    /**
     * SecretAccessKey - 申请时返回的明文密钥
     * <p>请替换为实际的 SecretAccessKey</p>
     */
    private static final String SECRET_ACCESS_KEY = "";

    /**
     * 客户端签名并发送 POST 请求
     *
     * <p>完整演示签名方法的客户端签名流程：</p>
     * <ol>
     *   <li>准备请求参数</li>
     *   <li>按规范构建签名</li>
     *   <li>组装 Authorization 头</li>
     *   <li>发送 HTTP 请求并打印响应</li>
     * </ol>
     *
     * @param uri         请求 URI（如 "/sign/verify"）
     * @param requestBody 请求体 JSON 字符串
     * @throws Exception 签名计算或网络请求异常
     */
    public static void sendSignedPostRequest(String uri, String requestBody) throws Exception {
        String httpMethod = "POST";
        String contentType = "application/json; charset=utf-8";

        // 当前 Unix 秒级时间戳
        long timestamp = System.currentTimeMillis() / 1000;

        System.out.println("========== 签名方法客户端签名流程 ==========");
        System.out.println("请求时间戳: " + timestamp);
        System.out.println("请求方法: " + httpMethod);
        System.out.println("请求URI: " + uri);
        System.out.println("请求体: " + requestBody);
        System.out.println();

        // ========== 第一步：构建规范请求串 ==========
        System.out.println("---------- 第一步：构建规范请求串 ----------");

        // 参与签名的请求头（按 key 字典序排列）
        TreeMap<String, String> signedHeaders = new TreeMap<>();
        signedHeaders.put("content-type", contentType);
        signedHeaders.put("host", HOST);

        // 计算请求体的 SHA-256 哈希
        String hashedPayload = SignatureUtil.sha256Hex(requestBody);
        System.out.println("请求体 SHA-256: " + hashedPayload);

        // 构建规范请求串
        String canonicalRequest = SignatureUtil.buildCanonicalRequest(
                httpMethod, uri, null, signedHeaders, hashedPayload);
        System.out.println("规范请求串:\n" + canonicalRequest);
        System.out.println();

        // ========== 第二步：构建待签名字符串 ==========
        System.out.println("---------- 第二步：构建待签名字符串 ----------");

        String hashedCanonicalRequest = SignatureUtil.sha256Hex(canonicalRequest);
        String stringToSign = SignatureUtil.buildStringToSign(
                timestamp, ACCESS_KEY_ID, hashedCanonicalRequest);
        System.out.println("AccessKeyId: " + ACCESS_KEY_ID);
        System.out.println("规范请求串 SHA-256: " + hashedCanonicalRequest);
        System.out.println("待签名字符串:\n" + stringToSign);
        System.out.println();

        // ========== 第三步：计算签名 ==========
        System.out.println("---------- 第三步：计算签名 ----------");

        String signature = SignatureUtil.calculateSignature(
                SECRET_ACCESS_KEY, ACCESS_KEY_ID, stringToSign);
        System.out.println("签名结果: " + signature);
        System.out.println();

        // ========== 第四步：组装 Authorization 头 ==========
        System.out.println("---------- 第四步：组装 Authorization 头 ----------");

        String signedHeaderKeys = String.join(";", signedHeaders.keySet());
        String authorization = SignatureUtil.buildAuthorization(
                ACCESS_KEY_ID, signedHeaderKeys, signature);
        System.out.println("Authorization: " + authorization);
        System.out.println();

        // ========== 第五步：发送 HTTP 请求 ==========
        System.out.println("---------- 第五步：发送 HTTP 请求 ----------");

        String fullUrl = BASE_URL + uri;
        System.out.println("请求URL: " + fullUrl);

        HttpURLConnection connection = (HttpURLConnection) new URL(fullUrl).openConnection();
        connection.setRequestMethod(httpMethod);
        connection.setRequestProperty("Content-Type", contentType);
        connection.setRequestProperty("Host", HOST);
        connection.setRequestProperty("X-Timestamp", String.valueOf(timestamp));
        connection.setRequestProperty("Authorization", authorization);
        connection.setDoOutput(true);

        // 写入请求体
        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestBody.getBytes(StandardCharsets.UTF_8));
        }

        // 读取响应
        int responseCode = connection.getResponseCode();
        System.out.println("响应状态码: " + responseCode);

        BufferedReader reader;
        if (responseCode >= 200 && responseCode < 300) {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        } else {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
        }

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        System.out.println("响应内容: " + response.toString());
        System.out.println();
    }

    /**
     * 客户端签名并发送 GET 请求
     *
     * @param uri         请求 URI（如 "/sign/verify"）
     * @param queryParams 查询参数（可为 null）
     * @throws Exception 签名计算或网络请求异常
     */
    public static void sendSignedGetRequest(String uri, TreeMap<String, String> queryParams) throws Exception {
        String httpMethod = "GET";
        String contentType = "application/json; charset=utf-8";

        long timestamp = System.currentTimeMillis() / 1000;

        System.out.println("========== GET 请求签名流程 ==========");
        System.out.println("请求时间戳: " + timestamp);
        System.out.println("请求URI: " + uri);
        System.out.println();

        // 参与签名的请求头
        TreeMap<String, String> signedHeaders = new TreeMap<>();
        signedHeaders.put("content-type", contentType);
        signedHeaders.put("host", HOST);

        // GET 请求体为空，对空字符串做 SHA-256
        String hashedPayload = SignatureUtil.sha256Hex("");

        // 构建规范请求串
        String canonicalRequest = SignatureUtil.buildCanonicalRequest(
                httpMethod, uri, queryParams, signedHeaders, hashedPayload);

        // 构建待签名字符串
        String hashedCanonicalRequest = SignatureUtil.sha256Hex(canonicalRequest);
        String stringToSign = SignatureUtil.buildStringToSign(
                timestamp, ACCESS_KEY_ID, hashedCanonicalRequest);

        // 计算签名
        String signature = SignatureUtil.calculateSignature(
                SECRET_ACCESS_KEY, ACCESS_KEY_ID, stringToSign);

        // 组装 Authorization 头
        String signedHeaderKeys = String.join(";", signedHeaders.keySet());
        String authorization = SignatureUtil.buildAuthorization(
                ACCESS_KEY_ID, signedHeaderKeys, signature);

        System.out.println("Authorization: " + authorization);

        // 构建完整 URL（带查询参数）
        String queryString = SignatureUtil.buildCanonicalQueryString(queryParams);
        String fullUrl = BASE_URL + uri;
        if (!queryString.isEmpty()) {
            fullUrl += "?" + queryString;
        }

        System.out.println("请求URL: " + fullUrl);

        // 发送请求
        HttpURLConnection connection = (HttpURLConnection) new URL(fullUrl).openConnection();
        connection.setRequestMethod(httpMethod);
        connection.setRequestProperty("Content-Type", contentType);
        connection.setRequestProperty("Host", HOST);
        connection.setRequestProperty("X-Timestamp", String.valueOf(timestamp));
        connection.setRequestProperty("Authorization", authorization);

        int responseCode = connection.getResponseCode();
        System.out.println("响应状态码: " + responseCode);

        BufferedReader reader;
        if (responseCode >= 200 && responseCode < 300) {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        } else {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
        }

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        System.out.println("响应内容: " + response.toString());
        System.out.println();
    }

    /**
     * 测试入口
     *
     * <p>运行前请确保：</p>
     * <ol>
     *   <li>服务端已启动</li>
     *   <li>已替换 ACCESS_KEY_ID 和 SECRET_ACCESS_KEY 为实际值</li>
     * </ol>
     */
    public static void main(String[] args) {
        try {
            // ==================== 测试1：POST 请求签名验证 ====================
            System.out.println("==================== 测试1：POST 请求签名验证 ====================\n");
            String postBody = "{\"data\":\"hello, signature!\"}";
            sendSignedPostRequest("/sign/verify", postBody);

            System.out.println("\n");

            // ==================== 测试2：GET 请求签名验证 ====================
            System.out.println("==================== 测试2：GET 请求签名验证 ====================\n");
            sendSignedGetRequest("/sign/verify", null);

        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
