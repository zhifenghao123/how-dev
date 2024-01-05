package com.howdev.common.encrypt;

import java.util.ArrayList;
import java.util.List;

/**
 * EncryptAlgorithmEnum class
 *
 * @author haozhifeng
 * @date 2023/12/08
 */
public enum EncryptAlgorithmEnum {
    /**
     * 凯撒加密
     */
    CAESAR("Caesar", EncryptTypeEnum.CAESAR, true),

    /**
     * Base64
     */
    Base64("Base64", EncryptTypeEnum.Base64, true),

    /**
     * MD5
     */
    MD5("MD5", EncryptTypeEnum.MDA, true),

    /**
     * SHA-1
     */
    SHA_1("SHA-1", EncryptTypeEnum.MDA, true),

    /**
     * SHA-256
     */
    SHA_256("SHA-256", EncryptTypeEnum.MDA, true),

    /**
     * DES
     */
    DES("DES", EncryptTypeEnum.SYMMETRY, true),

    /**
     * 3DES
     */
    TRIPLE_DES("3DES", EncryptTypeEnum.SYMMETRY, true),

    /**
     * AES
     */
    AES("AES", EncryptTypeEnum.SYMMETRY, true),

    /**
     * RSA
     */
    RSA("RSA", EncryptTypeEnum.ASYMMETRY, true),

    /**
     * DSA
     */
    DSA("DSA", EncryptTypeEnum.ASYMMETRY, true),

    /**
     * Diffie-Hellman (D-H) 密钥交换协议中的公钥加密算法
     */
    DIFFIE_HELLMAN("DIFFIEHELLMAN", EncryptTypeEnum.ASYMMETRY, true),

    /**
     * Elliptic Curve Cryptography（ECC,椭圆曲线加密算法）
     */
    ECC("ECC", EncryptTypeEnum.ASYMMETRY, true);


    /**
     * 算法名称
     */
    private final String algorithmName;
    /**
     * 加密类型
     */
    private final EncryptTypeEnum encryptType;

    private final boolean supported;

    EncryptAlgorithmEnum(String algorithmName, EncryptTypeEnum encryptType, boolean supported) {
        this.algorithmName = algorithmName;
        this.encryptType = encryptType;
        this.supported = supported;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public EncryptTypeEnum getEncryptType() {
        return encryptType;
    }

    public List<EncryptAlgorithmEnum> getByEncryptType(EncryptTypeEnum encryptType) {
        List<EncryptAlgorithmEnum> list = new ArrayList<>();
        for (EncryptAlgorithmEnum algorithm : EncryptAlgorithmEnum.values()) {
            if (algorithm.getEncryptType().equals(encryptType)) {
                list.add(algorithm);
            }
        }
        return list;
    }



    enum EncryptTypeEnum {
        /**
         * 凯撒加密
         */
        CAESAR(1, "凯撒加密"),
        /**
         * Base64
         */
        Base64(2, "Base64"),
        /**
         * 信息摘要加密算法
         */
        MDA(3,"信息摘要加密算法"),
        /**
         * 对称加密
         */
        SYMMETRY(4, "对称加密"),
        /**
         * 非对称加密
         */
        ASYMMETRY(5, "非对称加密");

        /**
         * 类型
         */
        private final Integer type;
        /**
         * 描述
         */
        private final String desc;

        EncryptTypeEnum(Integer type, String desc) {
            this.type = type;
            this.desc = desc;
        }
    }
}
