package com.howdev.jwtauth.service;

import com.howdev.jwtauth.bo.PermissionBo;
import com.howdev.jwtauth.bo.RoleAttachedPermissionsBo;
import com.howdev.jwtauth.bo.RoleBo;
import com.howdev.jwtauth.bo.RolePermissionBo;
import com.howdev.jwtauth.constant.RegexPatternConst;
import com.howdev.jwtauth.dto.*;
import com.howdev.jwtauth.entity.Permission;
import com.howdev.jwtauth.entity.Role;
import com.howdev.jwtauth.entity.RolePermission;
import com.howdev.jwtauth.repository.PermissionRepository;
import com.howdev.jwtauth.repository.RolePermissionRepository;
import com.howdev.jwtauth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    
    /**
     * 验证角色信息
     */
    private void validateRoleInfo(String roleName) {
        if (StringUtils.isEmpty(roleName)) {
            throw new IllegalArgumentException("Role name cannot be empty");
        }
        // 检查角色名是否合法：4-64个字符之间，只能包括英文字母、数字和+=.@_-
        if (!Pattern.matches(RegexPatternConst.NAME_PATTERN, roleName)) {
            log.warn("角色名不合法: {}", roleName);
            throw new IllegalArgumentException("Role name is invalid");
        }
        
        Optional<Role> existedByNameOptional = roleRepository.findByName(roleName);
        if (existedByNameOptional.isPresent()) {
            throw new IllegalArgumentException("Role with name " + roleName + " already exists");
        }
    }
    
    /**
     * 创建角色
     */
    public boolean createRole(CreatRoleReq creatRoleReq) {
        String roleName = creatRoleReq.getName();
        String description = creatRoleReq.getDescription();
        
        validateRoleInfo(roleName);
        
        LocalDateTime now = LocalDateTime.now();
        Role role = new Role();
        role.setName(roleName);
        role.setStatus(description);
        role.setCreateTime(now);
        role.setUpdateTime(now);
        roleRepository.save(role);
        return true;
    }
    
    /**
     * 更新角色
     */
    public boolean updateRole(UpdateRoleReq updateRoleReq) {
        Long roleId = updateRoleReq.getId();
        
        Optional<Role> existedRoleOptional = roleRepository.findById(roleId);
        if (!existedRoleOptional.isPresent()) {
            throw new IllegalArgumentException("Role not found with id: " + roleId);
        }
        Role existedRole = existedRoleOptional.get();
        
        String roleStatus = updateRoleReq.getStatus();
        
        LocalDateTime now = LocalDateTime.now();
        existedRole.setStatus(roleStatus);
        existedRole.setUpdateTime(now);
        roleRepository.save(existedRole);
        return true;
    }
    
    /**
     * 分页查询角色列表
     */
    public ListRolesResp listRoles(ListRolesReq request) {
        // 构建分页和排序信息
        Sort sort = Sort.by(Sort.Direction.DESC, request.getSortField());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        
        // 调用分页查询方法
        Page<Role> rolePage = roleRepository.findByConditions(
            request.getName(), 
            request.getStatus(), 
            pageable
        );
        
        ListRolesResp response = new ListRolesResp();
        response.setRoles(rolePage.getContent());
        response.setTotal(rolePage.getTotalElements());
        return response;
    }
    
    /**
     * 根据ID获取角色详情
     */
    public RoleAttachedPermissionsBo getRole(Long id) {
        Optional<Role> roleOptional = roleRepository.findById(id);
        if (!roleOptional.isPresent()) {
            throw new IllegalArgumentException("Role not found with id: " + id);
        }

        Role role = roleOptional.get();
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        RoleBo roleBo = new RoleBo();
        roleBo.setId(role.getId());
        roleBo.setName(role.getName());
        roleBo.setDescription(role.getDescription());
        roleBo.setStatus(role.getStatus());
        //roleBo.setCreateTime(sdf.format(role.getCreateTime()));
        //roleBo.setUpdateTime(sdf.format(role.getUpdateTime()));
        roleBo.setCreateTime(role.getCreateTime().format(formatter));
        roleBo.setUpdateTime(role.getUpdateTime().format(formatter));

        RoleAttachedPermissionsBo roleAttachedPermissionsBo = new RoleAttachedPermissionsBo();
        roleAttachedPermissionsBo.setRole(roleBo);


        String roleName = role.getName();
        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleNameIn(Collections.singletonList(roleName));

        List<RolePermissionBo> rolePermissionBos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(rolePermissions)) {
            List<String> permissionNames = rolePermissions.stream()
                    .map(RolePermission::getPermissionName)
                    .collect(Collectors.toList());
            List<Permission> permissions = permissionRepository.findByNameIn(permissionNames);
            Map<String, Permission> permissionMap = permissions.stream()
                    .collect(Collectors.toMap(Permission::getName, Function.identity()));

            List<PermissionBo> permissionBos = new ArrayList<>();
            for (RolePermission rolePermission : rolePermissions) {
                RolePermissionBo rolePermissionBo = new RolePermissionBo();
                rolePermissionBo.setId(rolePermission.getId());
                rolePermissionBo.setRoleName(rolePermission.getRoleName());
                rolePermissionBo.setPermissionName(rolePermission.getPermissionName());
                rolePermissionBo.setStatus(rolePermission.getStatus());
                //rolePermissionBo.setAttachCreateTime(sdf.format(rolePermission.getCreateTime()));
                //rolePermissionBo.setAttachUpdateTime(sdf.format(rolePermission.getUpdateTime()));
                rolePermissionBo.setAttachCreateTime(rolePermission.getCreateTime().format(formatter));
                rolePermissionBo.setAttachUpdateTime(rolePermission.getUpdateTime().format(formatter));

                Permission permission = permissionMap.get(rolePermission.getPermissionName());
                PermissionBo permissionBo = new PermissionBo();
                permissionBo.setId(permission.getId());
                permissionBo.setName(permission.getName());
                permissionBo.setUrl(permission.getUrl());
                permissionBo.setDescription(permission.getDescription());
                permissionBo.setStatus(permission.getStatus());
                //permissionBo.setCreateTime(sdf.format(permission.getCreateTime()));
                //permissionBo.setUpdateTime(sdf.format(permission.getUpdateTime()));
                permissionBo.setCreateTime(permission.getCreateTime().format(formatter));
                permissionBo.setUpdateTime(permission.getUpdateTime().format(formatter));

                rolePermissionBo.setPermission(permissionBo);

                rolePermissionBos.add(rolePermissionBo);
            }
        }
        roleAttachedPermissionsBo.setRolePermissions(rolePermissionBos);

        return roleAttachedPermissionsBo;
    }
    
    /**
     * 删除角色
     */
    public boolean deleteRole(Long id) {
        Optional<Role> roleOptional = roleRepository.findById(id);
        if (!roleOptional.isPresent()) {
            throw new IllegalArgumentException("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
        return true;
    }

    public boolean attachPermissionToRole(AttachPermissionToRoleReq request) {
        String reqRoleName = request.getRoleName();
        List<String> reqPermissionNames = request.getPermissionNames();

        // 检查角色是否存在
        Optional<Role> existedRoleOptional = roleRepository.findByName(reqRoleName);
        if (!existedRoleOptional.isPresent()) {
            throw new IllegalArgumentException("Role not found with name: " + reqRoleName);
        }

        // 检查权限是否存在
        List<Permission> existedPermissions = permissionRepository.findByNameIn(reqPermissionNames);
        if (existedPermissions.size() != reqPermissionNames.size()) {
            Map<String, Permission> permissionMap = existedPermissions.stream()
                    .collect(Collectors.toMap(Permission::getName, Function.identity()));
            List<String> missingPermissions = reqPermissionNames.stream()
                    .filter(name -> !permissionMap.containsKey(name))
                    .collect(Collectors.toList());
            if (!missingPermissions.isEmpty()) {
                throw new IllegalArgumentException("Permissions not found: " + missingPermissions);
            }
        }

        // 检查角色权限是否已经存在
        List<RolePermission> existedAttachRolePermissions = rolePermissionRepository.findByRoleNameAndPermissionNameIn(reqRoleName, reqPermissionNames);
        if (!existedAttachRolePermissions.isEmpty()) {
            // 已经存在角色权限的关联记录
            Map<String, RolePermission> existedAttachRolePermissionMap = existedAttachRolePermissions.stream()
                    .collect(Collectors.toMap(RolePermission::getPermissionName, Function.identity()));
            Set<String> existedAttachRolePermissionNames = existedAttachRolePermissionMap.keySet();
            Set<String> reqExistedAttachRolePermissionNames = reqPermissionNames.stream()
                    .filter(existedAttachRolePermissionNames::contains)
                    .collect(Collectors.toSet());
            if (!reqExistedAttachRolePermissionNames.isEmpty()) {
                throw new IllegalArgumentException("Role permissions already exist: " + reqExistedAttachRolePermissionNames);
            }
        }

        // 创建角色权限关联记录
        LocalDateTime now = LocalDateTime.now();
        List<RolePermission> rolePermissions = new ArrayList<>();
        for (Permission permission : existedPermissions) {
            RolePermission rolePermission = new RolePermission();

            rolePermission.setRoleName(reqRoleName);
            rolePermission.setPermissionName(permission.getName());
            rolePermission.setCreateTime(now);
            rolePermission.setUpdateTime(now);

            rolePermissions.add(rolePermission);

        }
        rolePermissionRepository.saveAll(rolePermissions);

        return true;
    }

    public boolean detachPermissionToRole(DetachPermissionToRoleReq request) {
        String reqRoleName = request.getRoleName();
        List<String> reqPermissionNames = request.getPermissionNames();

        // 检查角色是否存在
        if (request.getCheckPermissionExist()) {
            Optional<Role> existedRoleOptional = roleRepository.findByName(reqRoleName);
            if (!existedRoleOptional.isPresent()) {
                throw new IllegalArgumentException("Role not found with name: " + reqRoleName);
            }
        }
        if (request.getCheckPermissionExist()) {
            List<Permission> existedPermissions = permissionRepository.findByNameIn(reqPermissionNames);
            if (existedPermissions.size() != reqPermissionNames.size()) {
                Map<String, Permission> permissionMap = existedPermissions.stream()
                        .collect(Collectors.toMap(Permission::getName, Function.identity()));
                List<String> missingPermissions = reqPermissionNames.stream()
                        .filter(name -> !permissionMap.containsKey(name))
                        .collect(Collectors.toList());
                if (!missingPermissions.isEmpty()) {
                    throw new IllegalArgumentException("Permissions not found: " + missingPermissions);
                }
            }
        }

        // 检查角色权限是否存在
        List<RolePermission> existedAttachRolePermissions = rolePermissionRepository.findByRoleNameAndPermissionNameIn(reqRoleName, reqPermissionNames);
        // 没有找到角色权限的关联记录
        if (existedAttachRolePermissions.isEmpty()) {
            throw new IllegalArgumentException("Role permissions not exist: " + reqPermissionNames);
        }
        // 请求取消关联的权限在角色权限关联记录中不存在
        Map<String, RolePermission> existedAttachRolePermissionMap = existedAttachRolePermissions.stream()
                .collect(Collectors.toMap(RolePermission::getPermissionName, Function.identity()));
        Set<String> existedAttachRolePermissionNames = existedAttachRolePermissionMap.keySet();
        Set<String> reqMissingAttachRolePermissionNames = reqPermissionNames.stream()
                .filter(name -> !existedAttachRolePermissionNames.contains(name))
                .collect(Collectors.toSet());
        if (!reqMissingAttachRolePermissionNames.isEmpty()) {
            throw new IllegalArgumentException("Role permissions not exist: " + reqMissingAttachRolePermissionNames);
        }

        // 删除角色权限关联记录

        return rolePermissionRepository.deleteByRoleNameAndPermissionNameIn(reqRoleName, reqPermissionNames);
    }
}