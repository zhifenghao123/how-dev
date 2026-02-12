package com.howdev.jwtauth.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 超级管理员配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "super-admin")
public class SuperAdminConfig {
    
    /**
     * 超级管理员用户名
     */
    private String username;
    
    /**
     * 超级管理员密码
     */
    private String password;
}