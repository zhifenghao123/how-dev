package com.howdev.jwtauth.service;


import com.howdev.jwtauth.constant.RegexPatternConst;
import com.howdev.jwtauth.constant.TokenConst;
import com.howdev.jwtauth.dto.*;
import com.howdev.jwtauth.entity.Role;
import com.howdev.jwtauth.entity.User;
import com.howdev.jwtauth.entity.UserRole;
import com.howdev.jwtauth.enumeration.RetCodeEnum;
import com.howdev.jwtauth.exception.RetCodeException;
import com.howdev.jwtauth.processor.JwtProcessor;
import com.howdev.jwtauth.repository.RoleRepository;
import com.howdev.jwtauth.repository.UserRepository;
import com.howdev.jwtauth.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProcessor jwtProcessor;
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    /**
     * 用户注册
     * @param userRegisterReq 用户注册请求
     * @return 注册成功的用户信息，如果用户名已存在则返回null
     */
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

        // 5 生成JWT令牌
        log.debug("开始生成JWT令牌，用户名: {}", username);
        String accessToken = jwtProcessor.generateAccessToken(username);
        String refreshToken = jwtProcessor.generateRefreshToken(username);

        // 将access token和refresh token都保存到Redis
        String accessTokenKey = TokenConst.ACCESS_TOKEN_REDIS_KEY_PREFIX + username;
        String refreshTokenKey = TokenConst.REFRESH_TOKEN_REDIS_KEY_PREFIX + username;

        redisTemplate.opsForValue().set(accessTokenKey, accessToken,
                refreshTokenExpiration / 2, java.util.concurrent.TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue().set(refreshTokenKey, refreshToken,
                refreshTokenExpiration, java.util.concurrent.TimeUnit.MILLISECONDS);

        log.info("用户 {} 登录成功，生成AccessToken和RefreshToken", username);

        UserRegisterResp userRegisterResp = new UserRegisterResp();
        userRegisterResp.setUsername(username);
        userRegisterResp.setAccessToken(accessToken);
        userRegisterResp.setRefreshToken(refreshToken);

        return userRegisterResp;
        
    }

    public User getUserInfo(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new RetCodeException(RetCodeEnum.FAILED, "用户不存在");
        }
        return userOptional.get();
    }

    public Boolean grantRoleToUser(GrantRoleToUserReq request) {
        String reqUserId = request.getUserId();
        List<String> reqRoleNames = request.getRoleNames();

        // 1. 校验用户是否存在
        Optional<User> userOptional = userRepository.findByUserId(reqUserId);
        if (!userOptional.isPresent()) {
            throw new RetCodeException(RetCodeEnum.ILLEGAL_ARGUMENT, "用户不存在");
        }

        // 2. 校验角色是否存在
        List<Role> existedRoles = roleRepository.findByNameIn(reqRoleNames);
        if (existedRoles.isEmpty()) {
            throw new RetCodeException(RetCodeEnum.ILLEGAL_ARGUMENT, "角色不存在");
        }
        if (existedRoles.size() < reqRoleNames.size()) {
            Map<String, Role> roleMap = new HashMap<>();
            for (Role role : existedRoles) {
                roleMap.put(role.getName(), role);
            }
            List<String> notExistedRoles = new ArrayList<>();
            for (String roleName : reqRoleNames) {
                if (!roleMap.containsKey(roleName)) {
                    notExistedRoles.add(roleName);
                }
            }
            throw new RetCodeException(RetCodeEnum.ILLEGAL_ARGUMENT, "以下角色不存在: " + notExistedRoles);
        }

        // 3. 校验用户是否已经拥有该角色
        List<UserRole> existedUserRoles = userRoleRepository.findByUserIdAndRoleNameIn(reqUserId, reqRoleNames);
        if (!existedUserRoles.isEmpty()) {
            Set<String> existedRoleNames = new HashSet<>();
            for (UserRole userRole : existedUserRoles) {
                existedRoleNames.add(userRole.getRoleName());
            }
            throw new RetCodeException(RetCodeEnum.ILLEGAL_ARGUMENT, "用户已拥有以下角色: " + existedRoleNames);
        }

        // 4. 保存用户角色
        LocalDateTime now = LocalDateTime.now();
        List<UserRole> userRoles = new ArrayList<>();
        for (String reqRoleName : reqRoleNames) {
            UserRole userRole = new UserRole();
            userRole.setUserId(reqUserId);
            userRole.setRoleName(reqRoleName);
            userRole.setCreateTime(now);
            userRole.setUpdateTime(now);
            userRoles.add(userRole);
        }
        userRoleRepository.saveAll(userRoles);

        return true;
    }

    public Boolean revokeRoleFromUser(GrantRoleToUserReq request) {
        String reqUserId = request.getUserId();
        List<String> reqRoleNames = request.getRoleNames();

        // 1. 校验用户是否存在
        Optional<User> userOptional = userRepository.findByUserId(reqUserId);
        if (!userOptional.isPresent()) {
            throw new RetCodeException(RetCodeEnum.ILLEGAL_ARGUMENT, "用户不存在");
        }

        // 2. 校验角色是否存在
        List<Role> existedRoles = roleRepository.findByNameIn(reqRoleNames);
        if (existedRoles.isEmpty()) {
            throw new RetCodeException(RetCodeEnum.ILLEGAL_ARGUMENT, "角色不存在");
        }
        if (existedRoles.size() < reqRoleNames.size()) {
            Map<String, Role> roleMap = new HashMap<>();
            for (Role role : existedRoles) {
                roleMap.put(role.getName(), role);
            }
            List<String> notExistedRoles = new ArrayList<>();
            for (String roleName : reqRoleNames) {
                if (!roleMap.containsKey(roleName)) {
                    notExistedRoles.add(roleName);
                }
            }
            throw new RetCodeException(RetCodeEnum.ILLEGAL_ARGUMENT, "以下角色不存在: " + notExistedRoles);
        }

        // 3. 校验用户是否已经拥有该角色
        List<UserRole> existedUserRoles = userRoleRepository.findByUserIdAndRoleNameIn(reqUserId, reqRoleNames);
        if (existedUserRoles.isEmpty()) {
            throw new RetCodeException(RetCodeEnum.ILLEGAL_ARGUMENT, "用户未拥有以下角色: " + reqRoleNames);
        }
        List<String> existedUserRoleNames = existedUserRoles.stream().map(UserRole::getRoleName).collect(Collectors.toList());

        List<String> notExistedRoleNames = reqRoleNames.stream().filter(roleName -> !existedUserRoleNames.contains(roleName)).collect(Collectors.toList());
        if (!notExistedRoleNames.isEmpty()) {
            throw new RetCodeException(RetCodeEnum.ILLEGAL_ARGUMENT, "用户未拥有以下角色: " + notExistedRoleNames);
        }

        // 4. 删除用户角色
        userRoleRepository.deleteInBatch(existedUserRoles);
        return true;
    }

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
