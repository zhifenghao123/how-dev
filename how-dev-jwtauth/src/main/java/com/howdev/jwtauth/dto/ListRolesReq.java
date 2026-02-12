package com.howdev.jwtauth.dto;

import lombok.Data;

@Data
public class ListRolesReq {
    /**
     * 页码（从0开始）
     */
    private Integer page = 0;
    
    /**
     * 每页大小
     */
    private Integer size = 10;
    
    /**
     * 排序字段
     */
    private String sortField = "id";
    
    /**
     * 角色名称（模糊查询）
     */
    private String name;
    
    /**
     * 角色状态（精确查询）
     */
    private String status;
}