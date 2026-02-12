package com.howdev.jwtauth.dto;

import lombok.Data;

import java.util.List;

@Data
public class AttachPermissionToRoleReq {
    private String roleName;
    private List<String> permissionNames;
}
