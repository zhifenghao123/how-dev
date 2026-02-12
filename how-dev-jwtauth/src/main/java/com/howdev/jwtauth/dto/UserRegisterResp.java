package com.howdev.jwtauth.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterResp {
    /**
     * 用户名，只在注册时设置使用，后续不可修改
     */
    private String username;
    /**
     * 访问token
     */
    private String accessToken;
    /**
     * 刷新token
     */
    private String refreshToken;
}
