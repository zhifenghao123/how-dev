package com.howdev.jwtauth.secutity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.howdev.jwtauth.dto.BaseResponse;
import com.howdev.jwtauth.enumeration.RetCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证入口点 - 处理未认证的请求
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, 
                        HttpServletResponse response, 
                        AuthenticationException authException) throws IOException {
        
        log.warn("未认证的访问请求: {} {}", request.getMethod(), request.getRequestURI());
        
        //Result<Object> result = Result.error(401, "请先登录");
        BaseResponse<String> result = BaseResponse.newFailResponse(RetCodeEnum.FAILED.getErrorCode(), "请先登录");
        
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}