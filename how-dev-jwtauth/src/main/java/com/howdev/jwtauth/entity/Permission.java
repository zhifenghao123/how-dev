package com.howdev.jwtauth.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 权限实体
 */
@Data
@EqualsAndHashCode
@Entity
@Table(name = "t_permission")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false, length = 64)
    private String name;

    @Column(name = "url", nullable = false, length = 128)
    private String url;

    @Column(name = "description", length = 256)
    private String description;

    @Column(name = "status", length = 16)
    private String status = "ENABLE";

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

}