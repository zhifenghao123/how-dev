package com.howdev.iam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 临时访问密钥实体
 */
@Data
@EqualsAndHashCode
@Entity
@Table(name = "t_temp_access_key")
public class TempAccessKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "temp_access_key_id", unique = true, nullable = false, length = 32)
    private String tempAccessKeyId;

    @Column(name = "temp_access_secret_key", nullable = false, columnDefinition = "TEXT")
    private String tempAccessSecretKey;

    @Column(name = "temp_access_token", nullable = false, columnDefinition = "TEXT")
    private String tempAccessToken;

    @Column(name = "key_create_time", nullable = false)
    private Long keyCreateTime;

    @Column(name = "key_expire_time", nullable = false)
    private Long keyExpireTime;

    @Column(name = "distribute_base", nullable = false)
    private Integer distributeBase;

    @Column(name = "seed_key_id", nullable = false, length = 32)
    private String seedKeyId;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

}
