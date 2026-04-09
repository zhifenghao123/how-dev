package com.howdev.iam.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * HashUtil class
 *
 * @author haozhifeng
 * @date 2023/02/12
 */
public class HashUtil {
    static final long[] BYTE_TABLE = createLookupTable();

    private HashUtil() {
        // no need to do
    }

    public static Long getHashCode(String str) {
        return hashCode(str);
    }

    public static long hashCode(CharSequence cs) {
        long h = -4953706369002393500L;
        long[] ht = BYTE_TABLE;
        int len = cs.length();

        for (int i = 0; i < len; ++i) {
            char ch = cs.charAt(i);
            h = h * 7664345821815920749L ^ ht[ch & 255];
            h = h * 7664345821815920749L ^ ht[ch >>> 8 & 255];
        }

        return h;
    }

    /**
     * SHA-256哈希算法
     * @param input 输入字符串
     * @return 十六进制格式的SHA-256哈希值
     */
    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    private static long[] createLookupTable() {
        long[] byteTable = new long[256];
        long h = 6074001001750140548L;

        for (int i = 0; i < 256; ++i) {
            for (int j = 0; j < 31; ++j) {
                h ^= h >>> 7;
                h ^= h << 11;
                h ^= h >>> 10;
            }
            byteTable[i] = h;
        }
        return byteTable;
    }
}
