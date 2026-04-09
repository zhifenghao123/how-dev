package com.howdev.iam.handler;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 常规的对称加解密工具类（基于 AES-128-CBC NoPadding）
 *
 * <h3>算法概述</h3>
 * <p>本工具类提供基于 AES-128-CBC 模式的对称加解密能力，采用 NoPadding 填充方式，
 * 通过自定义的随机填充机制来满足 AES 块对齐要求（16 字节的倍数）。</p>
 *
 * <h3>加密后数据结构</h3>
 * <p>加密前的明文会被组装为以下结构，然后整体进行 AES 加密：</p>
 * <pre>
 * +-------------------+-------------------+-------------------+-------------------+
 * | 4 字节（大端序）   | lRandoms          | 原始明文           | 右填充随机串       |
 * | lRandoms 的长度    | （随机左填充串）    | （plaintext）      | （凑齐 32 的倍数） |
 * +-------------------+-------------------+-------------------+-------------------+
 * </pre>
 *
 * <h3>密钥与 IV 的关系</h3>
 * <ul>
 *   <li>AES 密钥：取传入 key 的前 16 字节</li>
 *   <li>初始化向量 IV：同样取传入 key 的前 16 字节（即密钥与 IV 相同）</li>
 * </ul>
 *
 * <h3>安全说明</h3>
 * <ul>
 *   <li>随机左填充串使得相同明文每次加密产生不同密文，增强安全性</li>
 *   <li>解密时固定从偏移位置提取 32 字节作为原文，因此<b>明文长度应为 32 字节</b></li>
 *   <li>传入的 key 长度至少为 16 字节，超出部分不参与加密运算</li>
 * </ul>
 */
public class SimpleDesUtil {

    private static final String AES_ALGORITHM = "AES/CBC/NoPadding";

    // ======================== 基础加解密 ========================

    /**
     * 基础加密方法
     *
     * <h4>加密流程</h4>
     * <ol>
     *   <li><b>生成随机左填充串</b>：调用 {@link AlphanumericRandom#generateFlexibleLengthRandomString(int)} 生成随机字符串 lRandoms，
     *       用于混淆明文，使相同明文每次加密结果不同</li>
     *   <li><b>组装待加密数据</b>：按以下顺序拼接字节数组：
     *       <pre>4 字节（lRandoms 长度，大端序） + lRandoms + plaintext</pre>
     *       例如：lRandoms = "abc"（3字节），plaintext = "Hello..."（32字节），
     *       则拼接为：[0x00, 0x00, 0x00, 0x03] + "abc" + "Hello..." = 共 39 字节</li>
     *   <li><b>右填充对齐</b>：计算距离 32 的倍数还差多少字节，用随机字符串填充。
     *       例如：39 字节，32 - (39 % 32) = 32 - 7 = 25，右填充 25 字节随机串，总长度变为 64 字节</li>
     *   <li><b>AES 加密</b>：使用 key 的前 16 字节同时作为 AES 密钥和 IV，执行 AES-128-CBC NoPadding 加密</li>
     *   <li><b>Base64 编码</b>：将加密后的字节数组编码为 Base64 字符串返回</li>
     * </ol>
     *
     * <h4>明文长度说明</h4>
     * <p>由于 {@link #decrypt(String, String)} 方法固定从解密数据中提取 32 字节作为原文，
     * 因此建议明文长度恰好为 32 字节。若明文不足或超过 32 字节，解密结果可能不正确。</p>
     *
     * @param key       加密密钥（至少 16 字节，超出部分不参与运算）
     * @param plaintext 待加密的明文（建议 32 字节）
     * @return Base64 编码的密文
     * @throws RuntimeException 加密失败时抛出
     */
    public static String encrypt(String key, String plaintext) {
        try {
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

            // ---- 第 1 步：生成随机左填充串，用于混淆明文 ----
            byte[] lRandoms = AlphanumericRandom.generateFlexibleLengthRandomString(0).getBytes(StandardCharsets.UTF_8);

            // ---- 第 2 步：组装待加密数据 ----
            // 结构：[4字节 lRandoms长度（大端序）] + [lRandoms] + [plaintext]
            byte[] textBytes = plaintext.getBytes(StandardCharsets.UTF_8);
            // 将 lRandoms 的长度编码为 4 字节大端序整数
            byte[] lenBytes = ByteBuffer.allocate(4).putInt(lRandoms.length).array();

            // 合并三部分字节数组
            byte[] combined = new byte[lenBytes.length + lRandoms.length + textBytes.length];
            System.arraycopy(lenBytes, 0, combined, 0, lenBytes.length);
            System.arraycopy(lRandoms, 0, combined, lenBytes.length, lRandoms.length);
            System.arraycopy(textBytes, 0, combined, lenBytes.length + lRandoms.length, textBytes.length);

            // ---- 第 3 步：右填充随机串，使总长度为 32 的倍数 ----
            int pad = 32 - (combined.length % 32);
            byte[] rRandoms =  AlphanumericRandom.generateFlexibleLengthRandomString(pad).getBytes(StandardCharsets.UTF_8);
            byte[] padded = new byte[combined.length + rRandoms.length];
            System.arraycopy(combined, 0, padded, 0, combined.length);
            System.arraycopy(rRandoms, 0, padded, combined.length, rRandoms.length);

            // ---- 第 4 步：AES-128-CBC NoPadding 加密 ----
            // IV 取 key 的前 16 字节（与密钥相同）
            byte[] iv = Arrays.copyOf(keyBytes, 16);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(Arrays.copyOf(keyBytes, 16), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] encrypted = cipher.doFinal(padded);

            // ---- 第 5 步：Base64 编码返回 ----
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("加密失败: " + e.getMessage(), e);
        }
    }

    /**
     * 基础解密方法
     *
     * <h4>解密流程（与 {@link #encrypt(String, String)} 完全对称）</h4>
     * <ol>
     *   <li><b>Base64 解码</b>：将密文字符串还原为加密后的字节数组</li>
     *   <li><b>AES 解密</b>：使用 key 的前 16 字节同时作为 AES 密钥和 IV，执行 AES-128-CBC NoPadding 解密，
     *       还原出加密前的完整数据结构</li>
     *   <li><b>提取 lRandoms 长度</b>：从解密数据的前 4 字节（大端序）读取随机左填充串的长度 len</li>
     *   <li><b>提取原始明文</b>：跳过前 4 字节和 len 字节的随机填充，从偏移 (len + 4) 处提取 32 字节作为原文</li>
     * </ol>
     *
     * <h4>数据提取示意</h4>
     * <pre>
     * 解密后数据：[4字节长度][lRandoms...][原始明文(32字节)][右填充随机串...]
     *             |← len →|  |← 偏移 len+4 →|
     *
     * 例如：len = 3，则从第 7 字节（索引 7）开始取 32 字节即为原始明文
     * </pre>
     *
     * <h4>长度校验</h4>
     * <p>解密后数据至少需要 40 字节（4 字节长度头 + 至少 4 字节随机填充 + 32 字节明文），
     * 否则抛出异常。</p>
     *
     * @param key        解密密钥（至少 16 字节，必须与加密时使用的密钥一致）
     * @param ciphertext Base64 编码的密文
     * @return 解密后的原始明文（32 字节）
     * @throws RuntimeException 解密失败或数据格式异常时抛出
     */
    public static String decrypt(String key, String ciphertext) {
        try {
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte[] iv = Arrays.copyOf(keyBytes, 16);

            // ---- 第 1 步：Base64 解码 ----
            byte[] encryptedBytes = Base64.getDecoder().decode(ciphertext);

            // ---- 第 2 步：AES-128-CBC NoPadding 解密 ----
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(Arrays.copyOf(keyBytes, 16), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            byte[] decrypted = cipher.doFinal(encryptedBytes);

            // ---- 第 3 步：校验解密数据长度 ----
            // 最小长度 = 4（长度头）+ 4（最少随机填充）+ 32（明文）= 40 字节
            if (decrypted.length < 40) {
                throw new RuntimeException("解密数据长度 " + decrypted.length + " < 40");
            }

            // ---- 第 4 步：提取 lRandoms 长度（前 4 字节，大端序） ----
            int len = ByteBuffer.wrap(decrypted, 0, 4).getInt();

            // ---- 第 5 步：从偏移 (len + 4) 处提取 32 字节原始明文 ----
            byte[] content = Arrays.copyOfRange(decrypted, len + 4, len + 4 + 32);
            return new String(content, StandardCharsets.UTF_8);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("解密失败: " + e.getMessage(), e);
        }
    }




    // ======================== 测试入口 ========================

    public static void testEncryptAndDecryptInDifferentKey() {
        System.out.println("【一、基础加解密 encrypt / decrypt 测试】");
        System.out.println("（注意：decrypt 固定从解密数据中取 32 字节作为原文，所以明文应为 32 字节）\n");

        // 测试用密钥（不同长度）
        String[] testKeys = {
                "1234567890abcdef",                         // 16 字节（最小有效长度）
                "1234567890abcdef12345678",                 // 24 字节
                "1234567890abcdef1234567890abcdef",         // 32 字节
                "abcdefghijklmnopqrstuvwxyz0123456789ABCD", // 40 字节（超长密钥，实际只取前16字节）
        };

        // 测试用明文（基础加解密固定取 32 字节）
        String plainText32 = "Hello1234567890abcdefghijklmnopq"; // 恰好 32 字节
        System.out.println("固定明文（32字节）: \"" + plainText32 + "\" (长度=" + plainText32.length() + ")\n");

        for (String testKey : testKeys) {
            try {
                String encrypted = encrypt(testKey, plainText32);
                System.out.println("加密结果: " + encrypted);
                String decrypted = decrypt(testKey, encrypted);
                System.out.println("解密结果: \"" + decrypted + "\"");
                boolean match = plainText32.equals(decrypted);
                System.out.println("验证: " + (match ? "✅ 通过" : "❌ 失败"));
            } catch (Exception e) {
                System.out.println("异常: " + e.getMessage());
            }
        }

        System.out.println("----------");
    }

    public static void testEncryptAndDecryptInDifferentPlainText() {
        System.out.println("【二、基础加解密 encrypt / decrypt 测试】");
        System.out.println("（注意：decrypt 固定从解密数据中取 32 字节作为原文，所以明文应为 32 字节）\n");

        // 测试用密钥（固定 16 字节）
        String testKey = "1234567890abcdef"; // 16 字节

        // 测试用明文（不同长度）
        String[] plainTexts = {
                "Hello",                                    // 5 字节（极短）
                "HelloWorld",                               // 10 字节
                "abcdefghijklmnopqrstuvwxyz12345",          // 30 字节
                "Hello1234567890abcdefghijklmnopq",         // 32 字节（恰好）
                "Hello1234567890abcdefghijklmnopqrstuvwxy", // 38 字节（超长）
        };

        for (String plainText : plainTexts) {
            try {
                String encrypted = encrypt(testKey, plainText);
                System.out.println("加密结果: " + encrypted);
                String decrypted = decrypt(testKey, encrypted);
                System.out.println("解密结果: \"" + decrypted + "\"");
                boolean match = plainText.equals(decrypted);
                System.out.println("验证: " + (match ? "✅ 通过" : "❌ 失败"));
            } catch (Exception e) {
                System.out.println("异常: " + e.getMessage());
            }
            System.out.println();
        }
    }

    public static void testEncryptAndDecryptV1InWrongKey() {
        System.out.println("\n========================================");
        System.out.println("【五、错误密钥/参数解密测试】\n");

        String plainText32 = "Hello1234567890abcdefghijklmnopq"; // 恰好 32 字节
        try {
            String enc = encrypt("1234567890abcdef", plainText32);
            System.out.println("正确密钥加密成功: " + enc);

            // 用错误密钥解密
            try {
                String wrongDec = decrypt("WrongKey1234567!", enc);
                System.out.println("错误密钥解密结果: \"" + wrongDec + "\"");
                System.out.println("验证: " + (plainText32.equals(wrongDec) ? "❌ 不应匹配但匹配了" : "✅ 预期不匹配"));
            } catch (Exception e) {
                System.out.println("错误密钥解密异常（预期行为）: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("加密异常: " + e.getMessage());
        }

    }





    public static void main(String[] args) {

        // -------------------- 基础加解密测试 --------------------
        testEncryptAndDecryptInDifferentKey();

        //测试不同明文长度对基础加解密的影响
        testEncryptAndDecryptInDifferentPlainText();

        // 错误密钥解密测试
        testEncryptAndDecryptV1InWrongKey();

    }
}
