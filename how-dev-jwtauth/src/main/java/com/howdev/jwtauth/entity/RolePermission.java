package com.howdev.jwtauth.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 角色权限关联实体
 */
@Data
@EqualsAndHashCode
@Entity
@Table(name = "t_role_permission")
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    @Column(name = "permission_name", nullable = false)
    private String permissionName;

    @Column(name = "status", nullable = false, length = 16)
    private String status = "ENABLE";

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;
}