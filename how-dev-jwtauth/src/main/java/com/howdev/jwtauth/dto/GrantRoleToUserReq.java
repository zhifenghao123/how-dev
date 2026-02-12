package com.howdev.jwtauth.dto;

import lombok.Data;

import java.util.List;

@Data
public class GrantRoleToUserReq {
    private String userId;
    private List<String> roleNames;
}
