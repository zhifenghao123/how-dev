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
@RequestMapping("/admin")
@RequiredArgsConstructor
@ApiException
public class AdminController {

    private final UserService userService;

    @GetMapping("/listUsers")
    public BaseResponse<ListUsersResp> listUsers(ListUsersReq req) {

        ListUsersResp listUsersResp = userService.listUsers(req);
        return BaseResponse.newSuccResponse(listUsersResp);
    }
}
