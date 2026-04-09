package com.howdev.iam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 访问密钥操作流水实体
 */
@Data
@EqualsAndHashCode
@Entity
@Table(name = "t_user_access_key_operation")
public class UserAccessKeyOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @Column(name = "access_key_id", nullable = false, length = 64)
    private String accessKeyId;

    @Column(name = "operation", nullable = false, length = 32)
    private String operation;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

}