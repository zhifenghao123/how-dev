package com.howdev.jwtauth.bo;

import com.howdev.jwtauth.entity.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class RoleBo {
    private Long id;

    private String name;

    private String description;

    private String status = "ENABLE";

    private String createTime;

    private String updateTime;
}
