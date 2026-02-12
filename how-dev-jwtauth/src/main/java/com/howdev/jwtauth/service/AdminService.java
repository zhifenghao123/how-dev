package com.howdev.jwtauth.service;


import com.howdev.jwtauth.entity.User;
import com.howdev.jwtauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 超级管理员授权服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 验证超级管理员凭据
     */
    public boolean validateSuperAdminCredentials(String username, String password) {
        // 这里可以添加额外的超级管理员验证逻辑
        // 比如检查IP白名单、二次验证等
        log.info("验证超级管理员凭据 - 用户名: {}", username);
        return true; // 默认通过验证，具体验证逻辑在CustomUserDetailsService中已实现
    }

    /**
     * 重置用户密码
     */
    public boolean resetUserPassword(String targetUsername, String newPassword) {
        log.info("超级管理员重置用户密码 - 目标用户: {}", targetUsername);
        
        Optional<User> userOptional = userRepository.findByUsername(targetUsername);
        if (!userOptional.isPresent()) {
            log.error("用户不存在: {}", targetUsername);
            return false;
        }
        
        User user = userOptional.get();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        
        userRepository.save(user);
        log.info("成功重置用户 {} 的密码", targetUsername);
        return true;
    }

    /**
     * 启用/禁用用户
     */
    public boolean toggleUserStatus(String targetUsername, boolean enabled) {
        log.info("超级管理员修改用户状态 - 目标用户: {}, 状态: {}", targetUsername, enabled ? "启用" : "禁用");
        
        Optional<User> userOptional = userRepository.findByUsername(targetUsername);
        if (!userOptional.isPresent()) {
            log.error("用户不存在: {}", targetUsername);
            return false;
        }
        
        User user = userOptional.get();
        user.setStatus(enabled ? "ENABLE" : "DISABLE");
        
        userRepository.save(user);
        log.info("成功{}用户 {}", enabled ? "启用" : "禁用", targetUsername);
        return true;
    }

    /**
     * 检查用户是否存在
     */
    public boolean userExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    /**
     * 获取用户信息
     */
    public User getUserInfo(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElse(null);
    }

    /**
     * 验证超级管理员操作权限
     */
    public boolean validateSuperAdminOperation(String currentUsername, String operation) {
        log.info("验证超级管理员操作权限 - 当前用户: {}, 操作: {}", currentUsername, operation);
        
        // 这里可以添加额外的权限验证逻辑
        // 比如操作频率限制、操作时间限制等
        
        return true; // 默认通过验证
    }
}