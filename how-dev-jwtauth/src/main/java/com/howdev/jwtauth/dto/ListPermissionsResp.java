package com.howdev.jwtauth.dto;

import com.howdev.jwtauth.entity.Permission;
import lombok.Data;

import java.util.List;

@Data
public class ListPermissionsResp {
    private List<Permission> permissions;
    private long total;
}
