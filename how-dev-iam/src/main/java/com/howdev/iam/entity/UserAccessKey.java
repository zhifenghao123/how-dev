package com.howdev.iam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户访问密钥实体
 */
@Data
@EqualsAndHashCode
@Entity
@Table(name = "t_user_access_key")
public class UserAccessKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @Column(name = "access_key_id", unique = true, nullable = false, length = 64)
    private String accessKeyId;

    @Column(name = "secret_access_key", nullable = false, length = 256)
    private String secretAccessKey;

    @Column(name = "status", nullable = false, length = 16)
    private String status = "ENABLE";

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

}