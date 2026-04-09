package com.howdev.iam.controller;

import com.howdev.iam.annotation.ApiException;
import com.howdev.iam.dto.*;
import com.howdev.iam.entity.User;
import com.howdev.iam.service.UserService;
import com.howdev.iam.util.HttpServletRequestUtil;
import lombok.RequiredArgsConstructor;
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

    private final UserService userService;

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
        //LoginResponse loginResponse = authService.login(loginRequest);
        //return BaseResponse.newSuccResponse(loginResponse);
        return BaseResponse.newSuccResponse(null);
    }

    @RequestMapping("/logout")
    public BaseResponse<String> logout() {
        // 从当前请求头中获取token
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String accessToken = HttpServletRequestUtil.getAccessTokenFromRequest(request);
        //authService.logout(accessToken);
        return BaseResponse.newSuccResponse("注销成功");
    }



    @GetMapping("/profile")
    public BaseResponse<User> getProfile() {

        //User profile = userService.getUserInfo(currentUser.getUsername());
        //return BaseResponse.newSuccResponse(profile);
        return BaseResponse.newSuccResponse(null);
    }
}
