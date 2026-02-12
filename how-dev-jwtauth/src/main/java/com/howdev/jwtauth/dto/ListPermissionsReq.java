package com.howdev.jwtauth.dto;

import lombok.Data;

@Data
public class ListPermissionsReq {
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
     * 权限URL（模糊查询）
     */
    private String url;
    
    /**
     * 权限名称（模糊查询）
     */
    private String name;
}
