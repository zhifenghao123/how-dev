package com.howdev.jwtauth.dto;

import lombok.Data;

@Data
public class CreatRoleReq {
    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色描述x
     */
    private String description;
}