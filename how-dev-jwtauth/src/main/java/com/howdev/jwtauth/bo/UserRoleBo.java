package com.howdev.jwtauth.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserRoleBo{
    private Long id;

    private Long userId;

    private String roleName;

    private String status;

    private String createTime;

    private String updateTime;
}
