package com.howdev.jwtauth.dto;

import lombok.Data;

@Data
public class UpdatePermissionReq {
    Long id;
    String name;
    String url;
}
