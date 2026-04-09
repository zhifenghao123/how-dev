package com.howdev.iam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 临时访问密钥种子实体
 */
@Data
@EqualsAndHashCode
@Entity
@Table(name = "t_temp_access_key_seed")
public class TempAccessKeySeed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seed_key_id", unique = true, nullable = false, length = 32)
    private String seedKeyId;

    @Column(name = "seed_key", nullable = false, length = 16)
    private String seedKey;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

}
