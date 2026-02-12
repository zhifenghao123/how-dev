package com.howdev.jwtauth.service;

import com.howdev.jwtauth.config.properties.SuperAdminConfig;
import com.howdev.jwtauth.entity.*;
import com.howdev.jwtauth.repository.PermissionRepository;
import com.howdev.jwtauth.repository.RolePermissionRepository;
import com.howdev.jwtauth.repository.UserRepository;
import com.howdev.jwtauth.repository.UserRoleRepository;
import com.howdev.jwtauth.secutity.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 自定义用户详情服务
 */
@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final SuperAdminConfig superAdminConfig;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(UserRepository userRepository,
                                    UserRoleRepository userRoleRepository,
                                    RolePermissionRepository rolePermissionRepository,
                                    PermissionRepository permissionRepository,
                                    SuperAdminConfig superAdminConfig,
                                    @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionRepository = permissionRepository;
        this.superAdminConfig = superAdminConfig;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        CustomUserDetails customUserDetails = new CustomUserDetails();
        // 首先检查是否为超级管理员(超级管理员的账号密码暂时不存储在数据库中，直接从配置文件中获取)
        if (isSuperAdmin(username)) {
            log.info("超级管理员 {} 登录系统", username);
            customUserDetails.setId(-1L);
            customUserDetails.setUsername(username);

            String superAdminPassword = passwordEncoder.encode(superAdminConfig.getPassword());
            customUserDetails.setPassword(superAdminPassword);

            customUserDetails.setName("超级管理员");
            customUserDetails.setStatus("ENABLE");
            customUserDetails.setSuperAdmin(true);
            return customUserDetails;
        }
        
        // 普通用户从数据库加载
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
        
        log.debug("Found user: {},", user.getUsername());

        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getUserId());
        List<Permission> permissions = new ArrayList<>();
        if (userRoles.isEmpty()) {
            log.warn("用户 {} 没有角色", username);
        } else {
            List<String> roleNames = userRoles.stream().map(UserRole::getRoleName).collect(Collectors.toList());
            log.debug("用户 {} 拥有的角色: {}", username, roleNames);
            List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleNameIn(roleNames);
            List<String> permissionNames = rolePermissions.stream().map(RolePermission::getPermissionName).collect(Collectors.toList());
            log.debug("用户 {} 拥有的权限: {}", username, permissionNames);
            permissions = permissionRepository.findByNameIn(permissionNames);
        }
        
        customUserDetails.setId(user.getId());
        customUserDetails.setUsername(user.getUsername());
        customUserDetails.setPassword(user.getPassword());
        customUserDetails.setName(user.getName());
        customUserDetails.setStatus(user.getStatus());
        customUserDetails.setUserRoles(userRoles);
        customUserDetails.setPermissions(permissions);
        customUserDetails.setSuperAdmin(false);
        return customUserDetails;

    }
    
    /**
     * 检查是否为超级管理员
     */
    private boolean isSuperAdmin(String username) {
        return superAdminConfig.getUsername() != null && 
               superAdminConfig.getUsername().equals(username);
    }

}