package com.howdev.iam.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class RoleAttachedPermissionsBo  {
    private RoleBo role;
    private List<RolePermissionBo> rolePermissions;
}
