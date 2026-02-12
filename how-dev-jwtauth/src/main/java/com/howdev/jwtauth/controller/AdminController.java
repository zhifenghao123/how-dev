package com.howdev.jwtauth.controller;


import com.howdev.jwtauth.annotation.ApiException;
import com.howdev.jwtauth.bo.RoleAttachedPermissionsBo;
import com.howdev.jwtauth.dto.*;
import com.howdev.jwtauth.entity.Permission;
import com.howdev.jwtauth.entity.User;
import com.howdev.jwtauth.enumeration.RetCodeEnum;
import com.howdev.jwtauth.secutity.CustomUserDetails;
import com.howdev.jwtauth.service.*;
import com.howdev.jwtauth.util.HttpServletRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;

/**
 * 超级管理员控制器
 * 专门处理超级管理员操作，与普通用户操作分离
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@ApiException
public class AdminController {

    private final AuthService authService;
    private final AdminService adminService;
    private final PermissionService permissionService;
    private final RoleService roleService;
    private final UserService userService;


    /**
     * 超级管理员专用接口
     */
    @GetMapping("/login")
    public BaseResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        return BaseResponse.newSuccResponse(loginResponse);
    }

    @RequestMapping("/logout")
    public BaseResponse<String> logout() {
        // 从当前请求头中获取token
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String accessToken = HttpServletRequestUtil.getAccessTokenFromRequest(request);
        authService.logout(accessToken);
        return BaseResponse.newSuccResponse("注销成功");
    }

    @RequestMapping("/refreshToken")
    public BaseResponse<TokenResponse> refreshToken() {
        // 从当前请求头中获取token
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String accessToken = HttpServletRequestUtil.getRefreshTokenFromRequest(request);
        TokenResponse tokenResponse = authService.refreshToken(accessToken);
        return BaseResponse.newSuccResponse(tokenResponse);
    }

    /**
     * 获取用户信息（超级管理员专用）
     */
    @GetMapping("/getUserInfo")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('admin:all')")
    public BaseResponse<Object> getUserInfo(@RequestBody GetUserInfoReq request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();

        if (!currentUser.isSuperAdmin()) {
            return BaseResponse.newSuccResponse("只有超级管理员才能查看用户信息");
        }

        User userInfo = adminService.getUserInfo(request.getUsername());
        return Optional.ofNullable(userInfo).<BaseResponse<Object>>map(BaseResponse::newSuccResponse).orElseGet(() -> BaseResponse.newFailResponse(RetCodeEnum.FAILED.getErrorCode(), "用户不存在: " + request.getUsername()));
    }

    @RequestMapping("/listUsers")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('admin:all')")
    public BaseResponse<ListUsersResp> listUsers(@RequestBody ListUsersReq request) {
        ListUsersResp result = userService.listUsers(request);
        return BaseResponse.newSuccResponse(result);
    }


    @RequestMapping("listPermissions")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('admin:all')")
    public BaseResponse<ListPermissionsResp> listPermissions(@RequestBody ListPermissionsReq request) {
        ListPermissionsResp result = permissionService.listPermissions(request);
        return BaseResponse.newSuccResponse(result);
    }

    @RequestMapping("getPermission")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('admin:all')")
    public BaseResponse<Object> getPermission(@RequestBody GetByIdReq request) {
        Permission permission = permissionService.getPermission(request.getId());
        return BaseResponse.newSuccResponse(permission);
    }

    @RequestMapping("createPermission")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('admin:all')")
    public BaseResponse<Object> createPermission(@RequestBody CreatPermissionReq request) {
        boolean result = permissionService.creatPermission(request);
        return BaseResponse.newSuccResponse(result);
    }

    @RequestMapping("updatePermission")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('admin:all')")
    public BaseResponse<Object> updatePermission(@RequestBody UpdatePermissionReq request) {
        boolean result = permissionService.updatePermission(request);
        return BaseResponse.newSuccResponse(result);
    }

    @RequestMapping("deletePermission")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('admin:all')")
    public BaseResponse<Object> deletePermission(@RequestBody GetByIdReq request) {
        boolean result = permissionService.deletePermission(request.getId());
        return BaseResponse.newSuccResponse(result);
    }

    // 角色管理接口
    @RequestMapping("listRoles")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('admin:all')")
    public BaseResponse<ListRolesResp> listRoles(@RequestBody ListRolesReq request) {
        ListRolesResp result = roleService.listRoles(request);
        return BaseResponse.newSuccResponse(result);
    }

    @RequestMapping("getRole")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('admin:all')")
    public BaseResponse<RoleAttachedPermissionsBo> getRole(@RequestBody GetByIdReq request) {
        RoleAttachedPermissionsBo role = roleService.getRole(request.getId());
        return BaseResponse.newSuccResponse(role);
    }

    @RequestMapping("createRole")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('admin:all')")
    public BaseResponse<Object> createRole(@RequestBody CreatRoleReq request) {
        boolean result = roleService.createRole(request);
        return BaseResponse.newSuccResponse(result);
    }

    @RequestMapping("updateRole")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('admin:all')")
    public BaseResponse<Object> updateRole(@RequestBody UpdateRoleReq request) {
        boolean result = roleService.updateRole(request);
        return BaseResponse.newSuccResponse(result);
    }

    @RequestMapping("deleteRole")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('admin:all')")
    public BaseResponse<Object> deleteRole(@RequestBody GetByIdReq request) {
        boolean result = roleService.deleteRole(request.getId());
        return BaseResponse.newSuccResponse(result);
    }

    @RequestMapping("attachPermissionToRole")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('admin:all')")
    public BaseResponse<Object> attachPermissionToRole(@RequestBody AttachPermissionToRoleReq request) {
        boolean result = roleService.attachPermissionToRole(request);
        return BaseResponse.newSuccResponse(result);
    }

    @RequestMapping("detachPermissionToRole")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('admin:all')")
    public BaseResponse<Object> detachPermissionToRole(@RequestBody DetachPermissionToRoleReq request) {
        boolean result = roleService.detachPermissionToRole(request);
        return BaseResponse.newSuccResponse(result);
    }


    @RequestMapping("grantRoleToUser")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('admin:all')")
    public BaseResponse<Object> grantRoleToUser(@RequestBody GrantRoleToUserReq request) {
        boolean result = userService.grantRoleToUser(request);
        return BaseResponse.newSuccResponse(result);
    }

    @RequestMapping("revokeRoleFromUser")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('admin:all')")
    public BaseResponse<Object> revokeRoleFromUser(@RequestBody GrantRoleToUserReq request) {
        boolean result = userService.revokeRoleFromUser(request);
        return BaseResponse.newSuccResponse(result);
    }

}