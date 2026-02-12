package com.howdev.jwtauth.secutity;

import com.howdev.jwtauth.entity.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 自定义用户详情
 */
@Data
public class CustomUserDetails implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private String name;
    private String status;
    private List<UserRole> userRoles;
    private List<Permission> permissions;
    private boolean isSuperAdmin = false;

    public CustomUserDetails() {
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        // 如果是超级管理员，添加所有权限
        if (isSuperAdmin) {
            authorities.add(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"));
            authorities.add(new SimpleGrantedAuthority("user:read"));
            authorities.add(new SimpleGrantedAuthority("user:write"));
            authorities.add(new SimpleGrantedAuthority("user:delete"));
            authorities.add(new SimpleGrantedAuthority("role:read"));
            authorities.add(new SimpleGrantedAuthority("role:write"));
            authorities.add(new SimpleGrantedAuthority("role:delete"));
            authorities.add(new SimpleGrantedAuthority("permission:read"));
            authorities.add(new SimpleGrantedAuthority("permission:write"));
            authorities.add(new SimpleGrantedAuthority("permission:delete"));
            authorities.add(new SimpleGrantedAuthority("admin:all"));
            return authorities;
        }
        
        // 普通用户的权限逻辑
        if (userRoles != null) {
            for (UserRole userRole : userRoles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + userRole.getRoleName()));
            }
        }

        if (permissions != null) {
            for (Permission permission : permissions) {
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }
        
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !"DISABLE".equals(status);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "ENABLE".equals(status);
    }
    
    /**
     * 检查是否为超级管理员
     */
    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }
}