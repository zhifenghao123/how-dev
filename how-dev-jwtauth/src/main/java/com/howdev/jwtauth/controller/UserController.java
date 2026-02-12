package com.howdev.jwtauth.controller;

import com.howdev.jwtauth.annotation.ApiException;
import com.howdev.jwtauth.dto.*;
import com.howdev.jwtauth.entity.User;
import com.howdev.jwtauth.secutity.CustomUserDetails;
import com.howdev.jwtauth.service.AuthService;
import com.howdev.jwtauth.service.RoleService;
import com.howdev.jwtauth.service.UserService;
import com.howdev.jwtauth.util.HttpServletRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 用户控制器
 * 专门处理普通用户操作
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@ApiException
public class UserController {

    private final AuthService authService;
    private final UserService userService;
    private final RoleService roleService;

    /**
     * 用户注册接口
     */
    @PostMapping("/register")
    public BaseResponse<UserRegisterResp> register(@RequestBody UserRegisterReq userRegisterReq) {
        UserRegisterResp registerResp = userService.register(userRegisterReq);
        return BaseResponse.newSuccResponse(registerResp);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public BaseResponse<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
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


    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('/user/profile')")
    public BaseResponse<User> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();

        User profile = userService.getUserInfo(currentUser.getUsername());
        return BaseResponse.newSuccResponse(profile);
    }

    @RequestMapping("/listRoles")
    @PreAuthorize("hasRole('role_query_system_role') or hasAuthority('/user/listRoles')")
    public BaseResponse<ListRolesResp> listRoles(@RequestBody ListRolesReq request) {
        ListRolesResp result = roleService.listRoles(request);
        return BaseResponse.newSuccResponse(result);
    }
}
