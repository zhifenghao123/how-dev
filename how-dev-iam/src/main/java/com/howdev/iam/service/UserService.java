package com.howdev.iam.service;

import com.howdev.iam.dto.ListUsersReq;
import com.howdev.iam.dto.ListUsersResp;
import com.howdev.iam.dto.UserRegisterReq;
import com.howdev.iam.dto.UserRegisterResp;
import com.howdev.iam.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     * @param userRegisterReq 用户注册请求
     * @return 注册成功的用户信息
     */
    UserRegisterResp register(UserRegisterReq userRegisterReq);

    /**
     * 获取用户信息
     * @param username 用户名
     * @return 用户信息
     */
    User getUserInfo(String username);

    /**
     * 分页查询用户列表
     * @param req 查询请求
     * @return 用户列表响应
     */
    ListUsersResp listUsers(ListUsersReq req);

}