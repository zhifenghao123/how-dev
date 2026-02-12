package com.howdev.jwtauth.dto;

import lombok.Data;

@Data
public class ListUsersReq {
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
     * 用户姓名（模糊查询）
     */
    private String name;

    /**
     * 用户状态（精确查询）
     */
    private String status;
}
