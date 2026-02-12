package com.howdev.jwtauth.bo;

import com.howdev.jwtauth.entity.Permission;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class PermissionBo {
    private Long id;

    private String name;

    private String url;

    private String description;

    private String status;

    private String createTime;

    private String updateTime;
}
