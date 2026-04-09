package com.howdev.iam.handler;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

/**
 * 基于属性加密的DES加解密工具
 *
 * <p>本工具实现了一种带随机混淆的 AES-128-CBC 加解密方案（V1 版本），核心思路如下：</p>
 * <ol>
 *   <li>加密时在明文的随机位置插入随机字符进行混淆，增加密文的随机性和抗分析能力</li>
 *   <li>通过位图（bitmap）记录插入位置，解密时按位图精确还原原文</li>
 *   <li>密钥由用户标识（userId）和时间戳（time）动态生成，不同用户/时间产生不同密钥</li>
 * </ol>
 *
 * <p><b>加密后数据结构：</b></p>
 * <pre>
 * ┌────────────────┬──────────────────┬──────────┬────────────────┬────────────────┐
 * │ sumPart1       │ 混淆后文本        │ padStr   │ sumPart2       │ padLen         │
 * │ (4字节)         │ (可变长度)        │ (可变长度) │ (4字节)         │ (4字节)         │
 * │ round(sum*0.2) │                  │          │ round(sum*0.8) │ padStr的长度    │
 * └────────────────┴──────────────────┴──────────┴────────────────┴────────────────┘
 * │←──────────────────── 总长度为 32 的整数倍 ──────────────────────────────────────→│
 * </pre>
 */
public class AttributeEncryptDesUtil {

    private static final String AES_ALGORITHM = "AES/CBC/NoPadding";
    private static final Random random = new Random();

    /**
     * 基础密钥配置
     * <p>实际使用时应从配置中心或安全存储中读取，这里仅为示例占位</p>
     */
    private static final String[] BASE_KEYS = {
            "abcdefghij123456",
            "bcdefghijk234567",
            "cdefghijkl345678",
            "defghijklm456789",
            "efghijklmn567890",
            "fghijklmno678901",
            "ghijklmnop789012"
    };

    /**
     * V1 版本加密方法
     *
     * <p><b>加密流程：</b></p>
     * <ol>
     *   <li><b>随机位置混淆</b>：在明文的随机位置插入随机字符，使得同一明文每次加密产生不同的密文</li>
     *   <li><b>记录位图 sum</b>：用一个整数的二进制位标记哪些位置插入了随机字符（bit N = 1 表示位置 N 被插入）</li>
     *   <li><b>数据拼接</b>：将位图拆分为两部分（20% 和 80%），分别放在数据头部和尾部，增加安全性</li>
     *   <li><b>对齐填充</b>：用随机字符串填充至 32 字节对齐（满足 AES 分组要求）</li>
 *   <li><b>AES 加密</b>：使用 getKey(userId, time) 生成的密钥进行 AES-128-CBC NoPadding 加密</li>
     *   <li><b>Base64 编码</b>：将加密结果编码为 Base64 字符串返回</li>
     * </ol>
     *
     * <p><b>位图 sum 的工作原理示例：</b></p>
     * <pre>
     * 假设明文 "ABCDE"，随机选中位置 [1, 3, 4]：
     *   sum += 1 &lt;&lt; 1  → bit 1 = 1
     *   sum += 1 &lt;&lt; 3  → bit 3 = 1
     *   sum += 1 &lt;&lt; 4  → bit 4 = 1
     *   sum = 26，二进制: 11010
     *   → 解密时逐位扫描即可知道哪些位置有插入的随机字符
     * </pre>
     *
     * <p><b>明文长度上限为 31 的原因：</b></p>
     * <p>位图 sum 通过两个 4 字节大端序整数存储（拆分为 20% 和 80%），总共能表示 32 个二进制位（bit 0 ~ bit 31）。
     * 候选插入位置范围为 [0, plaintextLen]，若 plaintextLen > 31，则 1 &lt;&lt; 32 会溢出，
     * 因此明文参与混淆的长度上限为 31。超过 31 字节的部分仍会被 AES 加密，但不参与随机混淆。</p>
     *
     * @param plaintext 待加密的明文（参与混淆的部分最长 31 字节）
     * @param userId    用户标识，用于密钥生成
     * @param timeStamp 时间戳，用于密钥生成
     * @return Base64 编码的密文
     * @throws RuntimeException 加密失败时抛出
     */
    public static String encrypt(String plaintext, int userId, long timeStamp) {
        try {
            // 明文参与混淆的长度上限为 31（受位图 32 位存储限制）
            int plaintextLen = Math.min(plaintext.length(), 31);

            // ========== 第一步：生成随机插入位置列表 ==========
            // 构建候选位置列表 [0, 1, 2, ..., plaintextLen]
            List<Integer> range = new ArrayList<>();
            for (int i = 0; i <= plaintextLen; i++) {
                range.add(i);
            }
            // 随机选取 1 ~ plaintextLen 个位置
            int pickCount = 1 + random.nextInt(plaintextLen);
            List<Integer> positions = new ArrayList<>(range);
            // 打乱顺序后取前 pickCount 个，等效于从候选列表中随机选取不重复的位置
            Collections.shuffle(positions, random);
            positions = positions.subList(0, pickCount);
            // 按降序排列：从大到小插入，确保前面的插入不影响后面字符的索引位置
            Collections.sort(positions);
            Collections.reverse(positions);

            // ========== 第二步：在随机位置插入随机字符，同时构建位图 sum ==========
            // sum 是一个位图（bitmap），每个二进制位对应一个位置：
            //   bit N = 1 表示位置 N 处插入了随机字符
            //   bit N = 0 表示位置 N 未被修改
            long sum = 0;
            StringBuilder plaintextWithRandomSb = new StringBuilder(plaintext);
            for (int pos : positions) {
                // 将 sum 的第 pos 位设为 1
                sum += 1L << pos;
                // 插入位置为 pos + 1（在 pos 之后插入）
                int insertPos = pos + 1;
                // 插入的随机字符长度由 insertPos 决定：insertPos % 7，若为 0 则取 1
                // 这是一个确定性规则，解密时可以通过相同公式计算出来，无需额外存储
                int randLen = (insertPos % 7 == 0) ? 1 : (insertPos % 7);
                String randStr = AlphanumericRandom.generateFlexibleLengthRandomString(randLen);
                if (insertPos >= plaintextWithRandomSb.length()) {
                    plaintextWithRandomSb.append(randStr);
                } else {
                    plaintextWithRandomSb.insert(insertPos, randStr);
                }
            }
            String plaintextWithRandom = plaintextWithRandomSb.toString();

            // ========== 第三步：计算填充长度，使最终数据总长度为 32 的整数倍 ==========
            // 最终数据结构：sumPart1(4) + 混淆文本(N) + padStr(P) + sumPart2(4) + padLen(4)
            // 总长度 = N + P + 12，需满足 (N + P + 12) % 32 == 0
            // 因此 P = 32 - (N + 12) % 32
            byte[] plaintextWithRandomBytes = plaintextWithRandom.getBytes(StandardCharsets.UTF_8);
            int plaintextWithRandomByteLen = plaintextWithRandomBytes.length;
            int padLen = 32 - ((plaintextWithRandomByteLen + 12) % 32);
            String padStr = AlphanumericRandom.generateFlexibleLengthRandomString(padLen);
            byte[] padBytes = padStr.getBytes(StandardCharsets.UTF_8);

            // ========== 第四步：拼接最终数据 ==========
            // 将位图 sum 拆分为两部分分散存储，增加安全性：
            //   头部存 round(sum * 0.2)，尾部存 round(sum * 0.8)
            //   解密时两部分相加即可还原完整的 sum
            byte[] sumPart1 = ByteBuffer.allocate(4).putInt((int) Math.round(sum * 0.2)).array();
            byte[] sumPart2 = ByteBuffer.allocate(4).putInt((int) Math.round(sum * 0.8)).array();
            byte[] padLenBytes = ByteBuffer.allocate(4).putInt(padLen).array();

            byte[] combined = new byte[4 + plaintextWithRandomBytes.length + padBytes.length + 4 + 4];
            int offset = 0;
            System.arraycopy(sumPart1, 0, combined, offset, 4);
            offset += 4;
            System.arraycopy(plaintextWithRandomBytes, 0, combined, offset, plaintextWithRandomBytes.length);
            offset += plaintextWithRandomBytes.length;
            System.arraycopy(padBytes, 0, combined, offset, padBytes.length);
            offset += padBytes.length;
            System.arraycopy(sumPart2, 0, combined, offset, 4);
            offset += 4;
            System.arraycopy(padLenBytes, 0, combined, offset, 4);

            // ========== 第五步：AES-128-CBC NoPadding 加密 ==========
            // 密钥和 IV 均由 getKey(userId, timeStamp) 生成，IV 取密钥的前 16 字节
            byte[] keyBytes = getKey(userId, timeStamp);
            byte[] iv = Arrays.copyOf(keyBytes, 16);

            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] encrypted = cipher.doFinal(combined);

            // 第六步：Base64 编码返回
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("V1加密失败: " + e.getMessage(), e);
        }
    }

    /**
     * V1 版本解密方法
     *
     * <p><b>解密流程（与加密流程完全互逆）：</b></p>
     * <ol>
 *   <li><b>Base64 解码 + AES 解密</b>：使用相同的 getKey(userId, time) 生成密钥，解密得到原始字节数据</li>
     *   <li><b>提取元数据</b>：从固定位置提取位图 sum（头4字节 + 倒数第8~5字节相加）和填充长度 padCount（末4字节）</li>
     *   <li><b>截取中间数据</b>：去掉首4字节和末8字节，得到"混淆文本 + 填充字符串"</li>
     *   <li><b>按位图还原</b>：从 pos=1 开始逐位扫描 sum，遇到 bit=1 则删除该位置的随机字符</li>
     *   <li><b>去掉尾部填充</b>：根据 padCount 截掉末尾的填充字符，得到原始明文</li>
     * </ol>
     *
     * <p><b>加密与解密的对称性：</b></p>
     * <pre>
     * 加密时：从大到小插入（positions 降序排列）
     *   → 后面的插入不影响前面字符的索引
     * 解密时：从小到大删除（pos 从 1 递增）
     *   → 前面的删除不影响后面字符的索引
     * 两者互为镜像操作，保证了还原的正确性。
     * </pre>
     *
     * <p><b>还原过程示例：</b></p>
     * <pre>
     * 假设加密时 sum = 26（二进制 11010），混淆后文本为 "ABfWCDTz7RExK9mQ"：
     *   pos=1: sum &amp; 1 = 0 → 跳过
     *   pos=2: sum &amp; 1 = 1 → 删除位置2起的2个字符("fW") → "ABCDTz7RExK9mQ"
     *   pos=3: sum &amp; 1 = 0 → 跳过
     *   pos=4: sum &amp; 1 = 1 → 删除位置4起的4个字符("Tz7R") → "ABCDExK9mQ"
     *   pos=5: sum &amp; 1 = 1 → 删除位置5起的5个字符("xK9mQ") → "ABCDE"
     *   sum=0 → 退出循环，还原完成
     * </pre>
     *
     * @param ciphertext Base64 编码的密文
     * @param userId     用户标识，用于密钥生成（必须与加密时一致）
     * @param timeStamp       时间戳，用于密钥生成（必须与加密时一致）
     * @return 解密后的原始明文
     * @throws RuntimeException 解密失败时抛出
     */
    public static String decrypt(String ciphertext, int userId, long timeStamp) {
        try {
            // ========== 第一步：AES-128-CBC 解密 ==========
            byte[] keyBytes = getKey(userId, timeStamp);
            byte[] iv = Arrays.copyOf(keyBytes, 16);

            byte[] encryptedBytes = Base64.getDecoder().decode(ciphertext);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            byte[] decrypted = cipher.doFinal(encryptedBytes);

            // 解密后数据至少 32 字节（最小的 32 对齐长度）
            if (decrypted.length < 32) {
                throw new RuntimeException("解密数据长度 " + decrypted.length + " < 32");
            }

            // ========== 第二步：提取元数据 ==========
            // 位图 sum 被拆分存储在头部和尾部，需要相加还原：
            //   sumPart1 = 前 4 字节（存储了 round(sum * 0.2)）
            //   sumPart2 = 倒数第 8~5 字节（存储了 round(sum * 0.8)）
            //   sum = sumPart1 + sumPart2
            // 使用 Integer.toUnsignedLong 转换，避免有符号整数的符号位问题
            int sumPart1 = ByteBuffer.wrap(decrypted, 0, 4).getInt();
            int sumPart2 = ByteBuffer.wrap(decrypted, decrypted.length - 8, 4).getInt();
            long sum = Integer.toUnsignedLong(sumPart1) + Integer.toUnsignedLong(sumPart2);

            // 填充长度 padCount = 末 4 字节，记录了加密时填充的随机字符串长度
            int padCount = ByteBuffer.wrap(decrypted, decrypted.length - 4, 4).getInt();

            // ========== 第三步：截取中间数据 ==========
            // 去掉首 4 字节（sumPart1）和末 8 字节（sumPart2 + padLen）
            // 剩余部分 = 混淆后文本 + 填充字符串
            byte[] middleBytes = Arrays.copyOfRange(decrypted, 4, decrypted.length - 8);
            StringBuilder sb = new StringBuilder(new String(middleBytes, StandardCharsets.UTF_8));

            // ========== 第四步：按位图 sum 逐位还原，删除插入的随机字符 ==========
            // 从 pos=1 开始，逐位检查 sum 的最低位：
            //   - 最低位为 1：该位置有插入的随机字符，按 (pos % 7) 规则计算长度并删除
            //   - 最低位为 0：该位置未被修改，跳过
            //   - 每次检查后 sum 右移一位，pos 递增
            int pos = 1;
            while (true) {
                if (sum == 0) {
                    break;
                }
                if ((sum & 1) == 1) {
                    // 删除的字符长度与加密时插入的长度一致：pos % 7，若为 0 则取 1
                    int removeLen = (pos % 7 == 0) ? 1 : (pos % 7);
                    if (pos < sb.length()) {
                        int endIdx = Math.min(pos + removeLen, sb.length());
                        sb.delete(pos, endIdx);
                    }
                }
                sum = sum >> 1;
                pos++;
            }

            // ========== 第五步：去掉尾部填充，返回原始明文 ==========
            String result = sb.toString();
            int endIdx = Math.max(0, result.length() - padCount);
            return result.substring(0, endIdx);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("V1解密失败: " + e.getMessage(), e);
        }
    }


    /**
     * 根据用户标识和时间戳动态生成 16 字节 AES 密钥
     *
     * <p><b>密钥生成流程：</b></p>
     * <ol>
     *   <li>计算 sum = userId + time</li>
     *   <li>对 sum 的字符串形式做 SHA-256 哈希，得到 32 字节的哈希值 sha</li>
     *   <li>根据 userId 从 BASE_KEYS 中选取一个基础密钥 baseKey</li>
     *   <li>逐位生成 16 字节密钥：检查 sum 的最低位，
     *       若为 1 则取 baseKey[i]，若为 0 则取 sha[i]，然后 sum 右移一位</li>
     * </ol>
     *
     * <p><b>密钥生成示例：</b></p>
     * <pre>
     * 假设 userId=1000, time=1565251177：
     *   sum = 1000 + 1565251177 = 1565252177
     *   sha = SHA-256("1565252177") 的原始字节
     *   baseKey = BASE_KEYS[1000 % 1000 % 7] = BASE_KEYS[0]
     *   sum 的二进制末 16 位决定每个字节取自 baseKey 还是 sha：
     *     bit 0 = 1 → key[0] = baseKey[0]
     *     bit 1 = 0 → key[1] = sha[1]
     *     ...依此类推
     * </pre>
     *
     * <p><b>设计意图：</b></p>
     * <ul>
     *   <li>不同的 userId + time 组合产生不同的 sum，从而生成不同的密钥</li>
     *   <li>密钥的每个字节来源于 baseKey 或 sha 的混合，增加了密钥的复杂度</li>
     *   <li>baseKey 由 userId 决定，sha 由 sum 决定，两者共同参与密钥构建</li>
     * </ul>
     *
     * @param userId 用户标识
     * @param timeStamp   时间戳
     * @return 16 字节的 AES 密钥
     * @throws RuntimeException 密钥生成失败时抛出
     */
    private static byte[] getKey(int userId, long timeStamp) {
        try {
            // 将 userId 和 time 相加，使用 long 避免 int 溢出
            long sum = (long) userId + (long) timeStamp;

            // 对 sum 的字符串形式做 SHA-256 哈希，得到 32 字节原始哈希值
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] sha = digest.digest(String.valueOf(sum).getBytes(StandardCharsets.UTF_8));

            // 根据 userId 选取基础密钥：userId % 1000 作为索引（对 BASE_KEYS 长度取模防越界）
            int baseKeyIndex = Math.abs(userId % 1000);
            if (BASE_KEYS.length == 0) {
                throw new RuntimeException("BASE_KEYS 未配置，请先配置基础密钥");
            }
            byte[] baseKey = BASE_KEYS[baseKeyIndex % BASE_KEYS.length].getBytes(StandardCharsets.UTF_8);

            // 逐位生成 16 字节密钥：
            // 检查 sum 的最低位决定当前字节取自 baseKey 还是 sha，然后 sum 右移一位
            byte[] key = new byte[16];
            long tempSum = sum;
            for (int i = 0; i < 16; i++) {
                key[i] = (tempSum & 1) == 1 ? baseKey[i] : sha[i];
                tempSum = tempSum >> 1;
            }
            return key;
        } catch (Exception e) {
            throw new RuntimeException("密钥生成失败: " + e.getMessage(), e);
        }
    }

    public static void testEncryptAndDecryptV1InUserIdTime() {
        System.out.println("========================================");
        System.out.println("【三、V1 版本加解密 encryptV1 / decryptV1 测试】");
        System.out.println("（需要 BASE_KEYS 已配置，否则会抛出异常）\n");

        // 不同 userId 和 time 组合
        int[][] userIdTimeParams = {
                {1000, 1565251177},   // 默认值
                {12345, 1700000000},  // 普通用户
                {999999, 1609459200}, // 大 userId
                {1, 1},              // 最小值
        };

        // 不同长度的明文
        String[] v1PlainTexts = {
                "Hi",                                       // 2 字节（极短）
                "HelloWorld",                               // 10 字节
                "abcdefghijklmnopqrstuvwxyz12345",          // 30 字节
                "Hello1234567890abcdefghijklmnopq",         // 32 字节（标准长度）
                "ThisIsAVeryLongTextThatExceeds32Bytes!!!",  // 40 字节（超长）
        };

        for (int[] param : userIdTimeParams) {
            int userId = param[0];
            int time = param[1];
            System.out.println("=== userId=" + userId + ", time=" + time + " ===\n");

            for (String v1PlainText : v1PlainTexts) {
                System.out.println("testEncryptAndDecryptV1, userId: " + userId + ", time: " + time + ", plainText: " + v1PlainText + "");
                try {
                    String encrypted = encrypt(v1PlainText, userId, time);
                    System.out.println("加密结果: " + encrypted);
                    String decrypted = decrypt(encrypted, userId, time);
                    System.out.println("解密结果: \"" + decrypted + "\"");
                    boolean match = v1PlainText.equals(decrypted);
                    System.out.println("验证: " + (match ? "✅ 通过" : "❌ 失败"));
                } catch (Exception e) {
                    System.out.println("异常: " + e.getMessage());
                }
                System.out.println("----------");
            }
        }
    }

    public static void testEncryptAndDecryptV1InWrongKey() {
        System.out.println("\n========================================");
        System.out.println("【五、错误密钥/参数解密测试】\n");

        String plainText32 = "Hello1234567890abcdefghijklmnopq"; // 恰好 32 字节
        try {
            String enc = encrypt(plainText32, 1000, 1565251177);
            System.out.println("正确密钥加密成功: " + enc);

            // 用错误密钥解密
            try {
                String wrongDec = decrypt(enc, 9999, 1565251177);
                System.out.println("错误密钥解密结果: \"" + wrongDec + "\"");
                System.out.println("验证: " + (plainText32.equals(wrongDec) ? "❌ 不应匹配但匹配了" : "✅ 预期不匹配"));
            } catch (Exception e) {
                System.out.println("错误密钥解密异常（预期行为）: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("加密异常: " + e.getMessage());
        }

    }

    public static void testEncryptAndDecryptV1InOnePlainTextMultiRoundEnc() {
        System.out.println("【四、V1 多次加解密一致性测试】");
        System.out.println("（同一明文多次加密，每次密文不同但解密结果应一致）\n");

        String consistencyText = "ConsistencyTestText1234567890ab"; // 31 字节
        int testUserId = 2024;
        int testTime = 1700000000;
        System.out.println("明文: \"" + consistencyText + "\" (长度=" + consistencyText.length() + ")");
        System.out.println("userId=" + testUserId + ", time=" + testTime + "\n");

        boolean allPassed = true;
        for (int round = 1; round <= 5; round++) {
            try {
                String enc = encrypt(consistencyText, testUserId, testTime);
                String dec = decrypt(enc, testUserId, testTime);
                boolean match = consistencyText.equals(dec);
                System.out.println("第 " + round + " 轮: 密文=" + enc.substring(0, 20) + "...  解密="
                        + (match ? "✅" : "❌ \"" + dec + "\""));
                if (!match) allPassed = false;
            } catch (Exception e) {
                System.out.println("第 " + round + " 轮: 异常 - " + e.getMessage());
                allPassed = false;
            }
        }
        System.out.println("\n一致性测试结果: " + (allPassed ? "✅ 全部通过" : "❌ 存在失败"));
    }


    public static void main(String[] args) {

        // -------------------- V1 版本加解密测试 --------------------
        testEncryptAndDecryptV1InUserIdTime();

        // -------------------- V1 多次加解密一致性测试 --------------------
        testEncryptAndDecryptV1InOnePlainTextMultiRoundEnc();

        // -------------------- 错误密钥解密测试 --------------------
        testEncryptAndDecryptV1InWrongKey();
    }
}