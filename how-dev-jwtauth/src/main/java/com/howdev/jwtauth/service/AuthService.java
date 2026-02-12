package com.howdev.jwtauth.service;


import com.howdev.jwtauth.constant.TokenConst;
import com.howdev.jwtauth.dto.LoginRequest;
import com.howdev.jwtauth.dto.LoginResponse;
import com.howdev.jwtauth.dto.TokenResponse;
import com.howdev.jwtauth.processor.JwtProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 认证服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProcessor jwtProcessor;
    private final AuthenticationManager authenticationManager;

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;


    /**
     * 登录
     */
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("用户登录请求: username={}", loginRequest.getUsername());

        try {
            String username = loginRequest.getUsername();

            // 检查用户是否已经登录
            String accessTokenKey = TokenConst.ACCESS_TOKEN_REDIS_KEY_PREFIX + username;
            String refreshTokenKey = TokenConst.REFRESH_TOKEN_REDIS_KEY_PREFIX + username;

            // 先检查access token是否有效
            String existingAccessToken = (String) redisTemplate.opsForValue().get(accessTokenKey);
            String existingRefreshToken = (String) redisTemplate.opsForValue().get(refreshTokenKey);

            // 如果access token存在且有效，直接返回现有token
            if (StringUtils.hasText(existingAccessToken) && jwtProcessor.validateToken(existingAccessToken)) {
                log.info("用户 {} access token有效，直接返回现有token", username);

                // 验证access token类型
                String accessTokenType = jwtProcessor.getTokenType(existingAccessToken);
                if ("access".equals(accessTokenType)) {
                    log.info("用户 {} 已登录且access token有效，返回现有token", username);
                    return new LoginResponse(
                            existingAccessToken,
                            existingRefreshToken,
                            "用户已登录，返回现有token"
                    );
                }
            }

            // 如果access token无效但refresh token有效，生成新的access token
            if (StringUtils.hasText(existingRefreshToken) && jwtProcessor.validateToken(existingRefreshToken)) {
                log.info("用户 {} refresh token有效，生成新的access token", username);

                // 验证refresh token类型
                String refreshTokenType = jwtProcessor.getTokenType(existingRefreshToken);
                if ("refresh".equals(refreshTokenType)) {
                    // 生成新的access token，使用现有的refresh token
                    String newAccessToken = jwtProcessor.generateAccessToken(username);

                    // 将新的access token保存到Redis
                    redisTemplate.opsForValue().set(accessTokenKey, newAccessToken,
                            refreshTokenExpiration / 2, java.util.concurrent.TimeUnit.MILLISECONDS);

                    log.info("用户 {} 已登录，返回新access token和现有refresh token", username);
                    return new LoginResponse(
                            newAccessToken,
                            existingRefreshToken,
                            "用户已登录，返回新token");
                }
            }

            log.debug("用户未登录或token无效，开始认证流程，创建认证Token");
            // 进行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            log.debug("认证成功，设置安全上下文");
            // 设置认证上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 生成JWT令牌
            username = authentication.getName();
            log.debug("开始生成JWT令牌，用户名: {}", username);
            String accessToken = jwtProcessor.generateAccessToken(username);
            String refreshToken = jwtProcessor.generateRefreshToken(username);

            // 将access token和refresh token都保存到Redis
            redisTemplate.opsForValue().set(accessTokenKey, accessToken,
                    refreshTokenExpiration / 2, java.util.concurrent.TimeUnit.MILLISECONDS);
            redisTemplate.opsForValue().set(refreshTokenKey, refreshToken,
                    refreshTokenExpiration, java.util.concurrent.TimeUnit.MILLISECONDS);

            log.info("用户 {} 登录成功，生成AccessToken和RefreshToken", username);

            return new LoginResponse(
                    accessToken,
                    refreshToken,
                    "登录成功"
            );

        } catch (BadCredentialsException e) {
            log.warn("用户名或密码错误: {}, 异常信息: {}", loginRequest.getUsername(), e.getMessage());
        } catch (UsernameNotFoundException e) {
            log.warn("用户不存在: {}, 异常信息: {}", loginRequest.getUsername(), e.getMessage());
        } catch (DisabledException e) {
            log.warn("账户已被禁用: {}, 异常信息: {}", loginRequest.getUsername(), e.getMessage());
        } catch (LockedException e) {
            log.warn("账户已被锁定: {}, 异常信息: {}", loginRequest.getUsername(), e.getMessage());
        } catch (AuthenticationException e) {
            log.error("认证失败: {}, 异常类型: {}, 异常信息: {}", loginRequest.getUsername(), e.getClass().getSimpleName(), e.getMessage());
        } catch (Exception e) {
            log.error("登录过程中发生未预期异常: username={}, 异常类型: {}, 异常信息: {}, 堆栈信息: ",
                    loginRequest.getUsername(), e.getClass().getSimpleName(), e.getMessage(), e);
        }
        return null;
    }

    /**
     * 登出
     */
    public void logout(String token) {
        if (!StringUtils.hasText(token)) {
            return;
        }

        try {
            String username = jwtProcessor.getUsernameFromToken(token);
            if (StringUtils.hasText(username)) {
                // 删除Redis中的RefreshToken和用户信息
                String refreshTokenKey = TokenConst.REFRESH_TOKEN_REDIS_KEY_PREFIX + username;
                String userInfoKey = TokenConst.USER_INFO_REDIS_KEY_PREFIX + username;


                redisTemplate.delete(refreshTokenKey);
                redisTemplate.delete(userInfoKey);

                log.info("用户 {} 登出成功", username);
            }
        } catch (Exception e) {
            log.error("登出过程中发生错误", e);
        }
    }

    /**
     * 刷新Token
     */
    public TokenResponse refreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new RuntimeException("RefreshToken不能为空");
        }

        // 验证RefreshToken
        if (!jwtProcessor.validateToken(refreshToken)) {
            throw new RuntimeException("RefreshToken无效");
        }

        // 检查Token类型
        String tokenType = jwtProcessor.getTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new RuntimeException("Token类型错误");
        }

        // 获取用户名
        String username = jwtProcessor.getUsernameFromToken(refreshToken);
        if (!StringUtils.hasText(username)) {
            throw new RuntimeException("无法从Token中获取用户信息");
        }

        // 检查Redis中的RefreshToken
        String refreshTokenKey = TokenConst.REFRESH_TOKEN_REDIS_KEY_PREFIX + username;
        String storedRefreshToken = (String) redisTemplate.opsForValue().get(refreshTokenKey);
        if (!refreshToken.equals(storedRefreshToken)) {
            throw new RuntimeException("RefreshToken已失效");
        }

        // 生成新的Token对
        String newAccessToken = jwtProcessor.generateAccessToken(username);
        String newRefreshToken = jwtProcessor.generateRefreshToken(username);

        // 更新Redis中的RefreshToken
        redisTemplate.opsForValue().set(refreshTokenKey, newRefreshToken, 
                refreshTokenExpiration, TimeUnit.MILLISECONDS);

        log.info("用户 {} 刷新Token成功", username);

        return new TokenResponse(newAccessToken, newRefreshToken, accessTokenExpiration / 1000);
    }
}