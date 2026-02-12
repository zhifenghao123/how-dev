package com.howdev.jwtauth.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
public class RolePermissionBo {

    private Long id;

    private String roleName;

    private String permissionName;

    private String status ;

    private String attachCreateTime;

    private String attachUpdateTime;

    private PermissionBo permission;
}
