package com.howdev.iam.handler;

import java.util.Random;

/**
 * 随机字符串生成器
 */
public class AlphanumericRandom {
    // 字符集，只包含大小写字母和数字
    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";

    private static final Random random = new Random();


    /**
     * 生成由字母和数字组成的随机字符串
     *
     * @param expectedLength 期望长度，若不在有效范围（1 ~ CHARSET.length()-2）内，则自动使用随机长度
     * @return 随机字母数字字符串
     */
    public static String generateFlexibleLengthRandomString(int expectedLength) {
        int strSize = CHARSET.length() - 1;
        if (expectedLength <= 0 || expectedLength >= strSize) {
            expectedLength = 1 + random.nextInt(strSize);
        }
        StringBuilder sb = new StringBuilder(expectedLength);
        for (int i = 0; i < expectedLength; i++) {
            sb.append(CHARSET.charAt(random.nextInt(strSize + 1)));
        }
        return sb.toString();
    }

    /**
     * 生成随机密钥
     * @param length 密钥长度
     * @return 生成的随机密钥
     */
    public static String generateFixedLengthRandomString(int length) {
        StringBuilder key = new StringBuilder(length);
        int max = CHARSET.length();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(max);
            key.append(CHARSET.charAt(index));
        }

        return key.toString();
    }

    public static String generateNanoTimeSeededRandomString(int length) {
        StringBuilder key = new StringBuilder(length);
        int max = CHARSET.length();

        long addedIndex = 0;
        for (int i = 0; i < length; i++) {
            long currentTimeNanos = System.nanoTime();
            //System.out.println("Round " + i + " currentTimeNanos:" + currentTimeNanos);
            addedIndex = addedIndex + currentTimeNanos;
            //System.out.println("Round " + i + " addedIndex:" + addedIndex);
            long index = (addedIndex + 173) % max;
            //System.out.println("Round " + i + " index:" + index);
            key.append(CHARSET.charAt((int) index));
            //System.out.println("Round " + i + " key:" + key);
        }
        return key.toString();
    }

    public static void main(String[] args) {
        System.out.println("generateFlexibleLengthRandomString(0):" + generateFlexibleLengthRandomString(0));
        System.out.println("generateFlexibleLengthRandomString(32):" + generateFlexibleLengthRandomString(32));
        System.out.println("generateFlexibleLengthRandomString(62):" + generateFlexibleLengthRandomString(62));
        System.out.println("generateFlexibleLengthRandomString(64):" + generateFlexibleLengthRandomString(64));

        System.out.println("generateFixedLengthRandomString(32):" + generateFixedLengthRandomString(32));
        System.out.println("generateNanoTimeSeededRandomString(32):" + generateNanoTimeSeededRandomString(32));


    }
}
