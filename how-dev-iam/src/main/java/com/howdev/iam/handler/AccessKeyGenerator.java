package com.howdev.iam.handler;

/**
 * AccessKey 生成器
 */
public class AccessKeyGenerator {
    private static final String ACCESS_KEY_ID_PREFIX = "AKID";

    /**
     * 生成带特定前缀的AccessKeyId
     * 前缀为AKID，后缀为32位随机字符串
     *
     * @return 带前缀的AccessKeyId
     */
    public static String generateAccessKeyId() {
        String randomString = AlphanumericRandom.generateNanoTimeSeededRandomString(32);
        return ACCESS_KEY_ID_PREFIX + randomString;
    }

    /**
     * 生成带特定前缀的SecretAccessKey
     *
     * @return 带前缀的SecretAccessKey
     */
    public static String generateSecretAccessKey() {
        return AlphanumericRandom.generateNanoTimeSeededRandomString(32);
    }


    /**
     * 测试方法
     */
    public static void main(String[] args) {
        // 测试AccessKeyId生成
        String accessKeyId = generateAccessKeyId();
        System.out.println("AccessKeyId: " + accessKeyId);

        // 测试SecretAccessKey生成
        long currentTime = System.currentTimeMillis();
        String secretAccessKey = generateSecretAccessKey();
        System.out.println("SecretAccessKey: " + secretAccessKey);
    }
}
