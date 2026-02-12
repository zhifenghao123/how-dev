package com.howdev.jwtauth.secutity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.howdev.jwtauth.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义JWT登录过滤器
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationLoginFilter(AuthenticationManager authenticationManager,
                                      JwtAuthenticationSuccessHandler authenticationSuccessHandler, String filterProcessesUrl) {
        super();
        setAuthenticationManager(authenticationManager);
        // 设置登录URL
        setFilterProcessesUrl(filterProcessesUrl);
        // 设置认证成功处理器，避免默认的重定向行为
        setAuthenticationSuccessHandler(authenticationSuccessHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, 
                                              HttpServletResponse response) 
            throws AuthenticationException {
        
        log.info("JWT登录过滤器开始处理登录请求，请求路径: {}, 请求方法: {}", 
                request.getRequestURI(), request.getMethod());
        
        if (!"POST".equals(request.getMethod())) {
            log.error("不支持的请求方法: {}", request.getMethod());
            throw new RuntimeException("不支持的请求方法: " + request.getMethod());
        }

        try {
            log.debug("开始解析登录请求JSON数据");
            // 从请求体中解析JSON格式的登录信息
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            
            log.info("解析登录请求成功，用户名: {}, 密码长度: {}", 
                    loginRequest.getUsername(), 
                    loginRequest.getPassword() != null ? loginRequest.getPassword().length() : 0);
            
            log.debug("开始创建认证Token");
            // 创建认证Token
            UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), 
                            loginRequest.getPassword()
                    );
            
            // 设置详细信息
            setDetails(request, authToken);
            
            log.debug("开始调用认证管理器进行认证");
            // 进行认证
            Authentication authentication = getAuthenticationManager().authenticate(authToken);
            
            log.info("用户 {} 认证成功", loginRequest.getUsername());
            return authentication;
            
        } catch (IOException e) {
            log.error("解析登录请求失败，异常信息: {}", e.getMessage(), e);
            throw new RuntimeException("解析登录请求失败", e);
        } catch (AuthenticationException e) {
            log.error("认证失败，用户名: {}, 异常类型: {}, 异常信息: {}", 
                    getUsernameFromRequest(request), e.getClass().getSimpleName(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("登录过程中发生未预期异常，异常类型: {}, 异常信息: {}", 
                    e.getClass().getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("登录过程异常", e);
        }
    }

    /**
     * 从请求中提取用户名（用于日志记录）
     */
    private String getUsernameFromRequest(HttpServletRequest request) {
        try {
            // 尝试重新读取请求体来获取用户名
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            return loginRequest.getUsername();
        } catch (Exception e) {
            return "unknown";
        }
    }
}