package com.howdev.jwtauth.secutity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.howdev.jwtauth.dto.BaseResponse;
import com.howdev.jwtauth.enumeration.RetCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT访问拒绝处理器 - 处理无权限的请求
 */
@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, 
                      HttpServletResponse response, 
                      AccessDeniedException accessDeniedException) throws IOException {
        
        log.warn("无权限访问请求: {} {}", request.getMethod(), request.getRequestURI());
        
        //Result<Object> result = Result.error(403, "权限不足");
        BaseResponse<String> result = BaseResponse.newFailResponse(RetCodeEnum.FAILED.getErrorCode(), "权限不足");
        
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}