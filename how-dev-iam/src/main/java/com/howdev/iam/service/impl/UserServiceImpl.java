package com.howdev.iam.service.impl;

import com.howdev.iam.constant.RegexPatternConst;
import com.howdev.iam.dto.ListUsersReq;
import com.howdev.iam.dto.ListUsersResp;
import com.howdev.iam.dto.UserRegisterReq;
import com.howdev.iam.dto.UserRegisterResp;
import com.howdev.iam.entity.User;
import com.howdev.iam.enumeration.RetCodeEnum;
import com.howdev.iam.exception.RetCodeException;
import com.howdev.iam.repository.UserRepository;
import com.howdev.iam.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserRegisterResp register(UserRegisterReq userRegisterReq) {
        // 1 校验用户名
        // 1.1 用户名不能为空
        if (StringUtils.isEmpty(userRegisterReq.getUsername())) {
            log.warn("用户名为空");
            throw new RetCodeException(RetCodeEnum.ILLEGAL_ARGUMENT, "用户名为空");
        }
        // 1.2 检查用户名是否合法：4-64个字符之间，只能包括英文字母、数字和+=.@_-
        if (!Pattern.matches(RegexPatternConst.NAME_PATTERN, userRegisterReq.getUsername())) {
            log.warn("用户名不合法: {}", userRegisterReq.getUsername());
            throw new RetCodeException(RetCodeEnum.ILLEGAL_ARGUMENT, "用户名不合法");
        }
        // 1.3检查用户名是否已存在
        String username = userRegisterReq.getUsername();
        if (userRepository.existsByUsername(username)) {
            log.warn("用户名已存在: {}", username);
            throw new RetCodeException(RetCodeEnum.ILLEGAL_ARGUMENT, "用户名已存在");
        }

        // 2 校验密码
        // 2.1 密码不能为空
        if (StringUtils.isEmpty(userRegisterReq.getPassword())) {
            log.warn("密码为空");
            throw new RetCodeException(RetCodeEnum.ILLEGAL_ARGUMENT, "密码为空");
        }
        // 2.2 检查密码是否合法：8-16个字符之间，只能包括英文字母、数字和+=.@_-
        if (!Pattern.matches(RegexPatternConst.NAME_PATTERN, userRegisterReq.getPassword())) {
            log.warn("密码不合法: {}", userRegisterReq.getPassword());
            throw new RetCodeException(RetCodeEnum.ILLEGAL_ARGUMENT, "密码不合法");
        }

        
        //String password = userRegisterReq.getPassword();
        String password = passwordEncoder.encode(userRegisterReq.getPassword());
        String name = userRegisterReq.getName();

        // 4 创建新用户并保存
        User user = new User();
        LocalDateTime now = LocalDateTime.now();
        user.setUserId(UUID.randomUUID().toString());
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name);
        user.setStatus("ENABLE");
        user.setCreateTime(now);
        user.setUpdateTime(now);
        try {
            // 保存用户
            User savedUser = userRepository.save(user);
        } catch (Exception e) {
            log.error("保存用户失败: {}", e.getMessage());
            throw new RetCodeException(RetCodeEnum.FAILED, "保存用户失败", e.getMessage());

        }

        log.info("用户 {} 登录成功，生成AccessToken和RefreshToken", username);

        UserRegisterResp userRegisterResp = new UserRegisterResp();
        userRegisterResp.setUsername(username);

        return userRegisterResp;
        
    }

    @Override
    public User getUserInfo(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new RetCodeException(RetCodeEnum.FAILED, "用户不存在");
        }
        return userOptional.get();
    }

    @Override
    public ListUsersResp listUsers(ListUsersReq req) {
        // 构建分页和排序信息
        Sort sort = Sort.by(Sort.Direction.DESC, req.getSortField());
        Pageable pageable = PageRequest.of(req.getPage(), req.getSize(), sort);
        
        // 调用分页查询方法
        Page<User> userPage = userRepository.findByConditions(
                req.getName(),
                req.getStatus(),
                pageable
        );
        
        ListUsersResp resp = new ListUsersResp();
        resp.setUsers(userPage.getContent());
        resp.setTotal(userPage.getTotalElements());
        return resp;
    }
}