package com.howdev.jwtauth.dto;

import lombok.Data;

import java.util.List;

@Data
public class DetachPermissionToRoleReq {
    private String roleName;
    private List<String> permissionNames;
    private Boolean checkRoleExist = true;
    private Boolean checkPermissionExist = true;
}
