package com.howdev.jwtauth.dto;

import com.howdev.jwtauth.entity.Role;
import lombok.Data;

import java.util.List;

@Data
public class ListRolesResp {
    /**
     * 角色列表
     */
    private List<Role> roles;
    
    /**
     * 总记录数
     */
    private long total;
}