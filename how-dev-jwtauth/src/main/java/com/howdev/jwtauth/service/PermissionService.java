package com.howdev.jwtauth.service;

import com.howdev.jwtauth.dto.CreatPermissionReq;
import com.howdev.jwtauth.dto.ListPermissionsReq;
import com.howdev.jwtauth.dto.ListPermissionsResp;
import com.howdev.jwtauth.dto.UpdatePermissionReq;
import com.howdev.jwtauth.entity.Permission;
import com.howdev.jwtauth.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;

    private void validatePermissionInfo(String permissionName, String permissionUrl) {

        if (StringUtils.isEmpty(permissionName)) {
            throw new IllegalArgumentException("Permission name cannot be empty");
        }


        if (StringUtils.isEmpty(permissionUrl)) {
            throw new IllegalArgumentException("Permission url cannot be empty");
        }

        Optional<Permission> existedByNameOptional = permissionRepository.findByName(permissionName);
        if (existedByNameOptional.isPresent()) {
            throw new IllegalArgumentException("Permission with name " + permissionName + " already exists");
        }

        Optional<Permission> existedByUrlOptional = permissionRepository.findByUrl(permissionUrl);
        if (existedByUrlOptional.isPresent()) {
            throw new IllegalArgumentException("Permission with url " + permissionUrl + " already exists");
        }
    }
    
    public boolean creatPermission(CreatPermissionReq creatPermissionReq) {
        String creatPermissionReqName = creatPermissionReq.getName();
        String creatPermissionReqUrl = creatPermissionReq.getUrl();
        validatePermissionInfo(creatPermissionReqName, creatPermissionReqUrl);

        LocalDateTime now = LocalDateTime.now();
        Permission permission = new Permission();
        permission.setName(creatPermissionReqName);
        permission.setUrl(creatPermissionReqUrl);
        permission.setCreateTime(now);
        permission.setUpdateTime(now);
        permissionRepository.save(permission);
        return true;
    }

    public boolean updatePermission(UpdatePermissionReq updatePermissionReq) {
        Long updatePermissionReqId = updatePermissionReq.getId();

        Optional<Permission> existedPermissionOptional = permissionRepository.findById(updatePermissionReqId);
        if (!existedPermissionOptional.isPresent()) {
            throw new IllegalArgumentException("Permission not found with id: " + updatePermissionReqId);
        }
        Permission existedPermission = existedPermissionOptional.get();

        String updatePermissionReqName = updatePermissionReq.getName();
        String updatePermissionReqUrl = updatePermissionReq.getUrl();

        validatePermissionInfo(updatePermissionReqName, updatePermissionReqUrl);

        LocalDateTime now = LocalDateTime.now();
        existedPermission.setName(updatePermissionReqName);
        existedPermission.setUrl(updatePermissionReqUrl);
        existedPermission.setCreateTime(now);
        existedPermission.setUpdateTime(now);
        permissionRepository.save(existedPermission);
        return true;
    }
    
    /**
     * 分页查询权限列表
     * @param request 查询请求参数
     * @return 分页的权限列表
     */
    public ListPermissionsResp listPermissions(ListPermissionsReq request) {
        // 构建分页和排序信息
        Sort sort = Sort.by(Sort.Direction.DESC, request.getSortField());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        
        // 调用分页查询方法
        Page<Permission> permissionPage = permissionRepository.findByConditions(
                request.getUrl(),
                request.getName(),
                pageable
        );
        ListPermissionsResp response = new ListPermissionsResp();
        response.setPermissions(permissionPage.getContent());
        response.setTotal(permissionPage.getTotalElements());
        return response;
    }

    public Permission getPermission(Long id) {
        Optional<Permission> permissionOptional = permissionRepository.findById(id);
        if (!permissionOptional.isPresent()) {
            throw new IllegalArgumentException("Permission not found with id: " + id);
        }
        return permissionOptional.get();
    }

    public boolean deletePermission(Long id) {
        Optional<Permission> permissionOptional = permissionRepository.findById(id);
        if (!permissionOptional.isPresent()) {
            throw new IllegalArgumentException("Permission not found with id: " + id);
        }
        permissionRepository.deleteById(id);
        return true;
    }
}