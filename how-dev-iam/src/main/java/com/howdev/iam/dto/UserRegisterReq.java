package com.howdev.iam.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRegisterReq {
    /**
     * 用户名，只在注册时设置使用，后续不可修改
     */
    private String username;
    private String password;
    /**
     * 姓名,后续可修改,用于展示
     */
    private String name;
}
