package com.howdev.jwtauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录响应DTO
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String message;
}