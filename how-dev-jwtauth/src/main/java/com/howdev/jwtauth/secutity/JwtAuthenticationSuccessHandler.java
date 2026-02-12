package com.howdev.jwtauth.secutity;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.howdev.jwtauth.constant.TokenConst;
import com.howdev.jwtauth.dto.BaseResponse;
import com.howdev.jwtauth.dto.TokenResponse;
import com.howdev.jwtauth.enumeration.RetCodeEnum;
import com.howdev.jwtauth.processor.JwtProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * JWT认证成功处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProcessor jwtProcessor;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      Authentication authentication) throws IOException {
        
        String username = authentication.getName();
        log.info("用户登录成功，开始处理认证成功逻辑，用户名: {}", username);
        
        try {
            log.debug("开始生成AccessToken");
            // 生成Token
            String accessToken = jwtProcessor.generateAccessToken(username);
            log.debug("开始生成RefreshToken");
            String refreshToken = jwtProcessor.generateRefreshToken(username);
            
            log.debug("Token生成完成，AccessToken长度: {}, RefreshToken长度: {}", 
                    accessToken.length(), refreshToken.length());
            
            // 将RefreshToken存储到Redis
            String refreshTokenKey = TokenConst.REFRESH_TOKEN_REDIS_KEY_PREFIX + username;
            log.debug("开始存储RefreshToken到Redis，Key: {}", refreshTokenKey);
            redisTemplate.opsForValue().set(refreshTokenKey, refreshToken, 
                    refreshTokenExpiration, TimeUnit.MILLISECONDS);
            
            // 将用户信息存储到Redis（可选）
            String userInfoKey = TokenConst.USER_INFO_REDIS_KEY_PREFIX + username;
            log.debug("开始存储用户信息到Redis，Key: {}", userInfoKey);
            redisTemplate.opsForValue().set(userInfoKey, authentication.getPrincipal(), 
                    refreshTokenExpiration, TimeUnit.MILLISECONDS);
            
            log.debug("Redis存储完成，过期时间: {}ms", refreshTokenExpiration);
            
            // 构建响应
            TokenResponse tokenResponse = new TokenResponse(
                    accessToken, 
                    refreshToken, 
                    refreshTokenExpiration / 1000
            );
            
            //Result<TokenResponse> result = Result.success(tokenResponse);
            BaseResponse<TokenResponse> result = BaseResponse.newSuccResponse(tokenResponse);
            
            log.debug("开始构建HTTP响应");
            // 设置响应
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(objectMapper.writeValueAsString(result));
            
            log.info("用户 {} 登录成功处理完成，返回Token响应", username);
            
        } catch (Exception e) {
            log.error("认证成功处理过程中发生异常，用户名: {}, 异常信息: {}", username, e.getMessage(), e);
            // 返回错误响应
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            //Result<String> errorResult = Result.error("系统内部错误，请稍后重试");
            BaseResponse<String> errorResult = BaseResponse.newFailResponse(RetCodeEnum.FAILED.getErrorCode(), "系统内部错误，请稍后重试");
            response.getWriter().write(objectMapper.writeValueAsString(errorResult));
        }
    }
}