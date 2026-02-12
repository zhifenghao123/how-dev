package com.howdev.jwtauth.dto;

import com.howdev.jwtauth.entity.Role;
import com.howdev.jwtauth.entity.RolePermission;
import lombok.Data;

import java.util.List;

@Data
public class GetRoleResp {
    private Role role;
    private List<RolePermission> rolePermissions;
}
