package com.howdev.jwtauth.repository;

import com.howdev.jwtauth.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRoleNameAndPermissionNameIn(String roleName, List<String> permissionNames);

    boolean deleteByRoleNameAndPermissionNameIn(String roleName, List<String> permissionNames);

    List<RolePermission> findByRoleNameIn(List<String> roleNames);
}
