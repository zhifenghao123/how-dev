package com.howdev.jwtauth.secutity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.howdev.jwtauth.dto.BaseResponse;
import com.howdev.jwtauth.enumeration.RetCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证失败处理器
 */
@Slf4j
@Component
public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      AuthenticationException exception) throws IOException {
        
        log.warn("用户认证失败: {}", exception.getMessage());
        
        String message;
        if (exception instanceof UsernameNotFoundException) {
            message = "用户不存在";
        } else if (exception instanceof BadCredentialsException) {
            message = "用户名或密码错误";
        } else if (exception instanceof LockedException) {
            message = "账户已被锁定";
        } else if (exception instanceof DisabledException) {
            message = "账户已被禁用";
        } else {
            message = "认证失败: " + exception.getMessage();
        }
        
        //Result<Object> result = Result.error(401, message);
        BaseResponse<String> result = BaseResponse.newFailResponse(RetCodeEnum.FAILED.getErrorCode(), message);
        
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}