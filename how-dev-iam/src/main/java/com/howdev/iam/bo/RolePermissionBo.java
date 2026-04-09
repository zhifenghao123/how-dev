package com.howdev.iam.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
