package com.howdev.jwtauth.dto;

import lombok.Data;

@Data
public class UpdateRoleReq {
    /**
     * 角色ID
     */
    private Long id;
    
    /**
     * 角色状态
     */
    private String status;
}