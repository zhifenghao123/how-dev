package com.howdev.jwtauth.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.howdev.jwtauth.dto.BaseResponse;
import com.howdev.jwtauth.enumeration.RetCodeEnum;
import com.howdev.jwtauth.secutity.JwtAuthenticationLoginFilter;
import com.howdev.jwtauth.secutity.JwtAuthenticationSuccessHandler;
import com.howdev.jwtauth.secutity.TokenAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 安全配置类
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;

    public SecurityConfig(@Lazy TokenAuthenticationFilter tokenAuthenticationFilter,
                          @Lazy JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler) {
        this.tokenAuthenticationFilter = tokenAuthenticationFilter;
        this.jwtAuthenticationSuccessHandler = jwtAuthenticationSuccessHandler;
    }
    /**
     * 配置安全过滤器链
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.debug("开始配置安全过滤器链");

        // 创建JWT普通用户登录过滤器
        JwtAuthenticationLoginFilter jwtUserLoginFilter = new JwtAuthenticationLoginFilter(
                authenticationManagerBean(),
                jwtAuthenticationSuccessHandler,
                "/user/login"
        );

        // 创建JWT管理员登录过滤器
        JwtAuthenticationLoginFilter jwtAdminLoginFilter = new JwtAuthenticationLoginFilter(
                authenticationManagerBean(),
                jwtAuthenticationSuccessHandler,
                "/admin/login"
        );


        http
                // 禁用CSRF
                .csrf().disable()
                // 配置会话管理为无状态
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 配置请求授权
                .authorizeRequests()
                // 允许匿名访问的接口
                .antMatchers("/").permitAll() // 首页
                //.antMatchers("/admin/login").permitAll() // 管理员登录页面
                //.antMatchers("/admin/logout").permitAll()
                .antMatchers("/admin/refreshToken").permitAll()
                .antMatchers("/user/login").permitAll() // 普通用户登录页面
                .antMatchers("/user/register").permitAll() // 普通用户注册页面
                .antMatchers("/user/logout").permitAll() // 普通用户退出登录
                .antMatchers("/user/refreshToken").permitAll() // 普通用户刷新令牌
                .antMatchers("/swagger-ui/**").permitAll() // Swagger UI
                .antMatchers("/v3/api-docs/**").permitAll() // OpenAPI文档
                .antMatchers("/error").permitAll() // 错误页面
                // 其他所有接口都需要认证
                .anyRequest().authenticated()
                .and()
                // 配置异常处理 - 确保ExceptionTranslationFilter能正确工作
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
                .authenticationEntryPoint(authenticationEntryPoint())
                .and()
                // 添加JWT普通用户登录过滤器
                .addFilterBefore(jwtUserLoginFilter, UsernamePasswordAuthenticationFilter.class)
                // 添加JWT管理员登录过滤器
                .addFilterBefore(jwtAdminLoginFilter, UsernamePasswordAuthenticationFilter.class)
                // 添加JWT认证过滤器
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        log.debug("安全过滤器链配置完成");
    }

    /**
     * 配置认证管理器
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        log.debug("配置认证管理器Bean");
        return super.authenticationManagerBean();
    }

    /**
     * 配置密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        log.debug("配置BCrypt密码编码器");
        // 也可用有参构造，取值范围是 4 到 31，默认值为 10。数值越大，加密计算越复杂
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置访问拒绝处理器
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response,
                               AccessDeniedException accessDeniedException) throws IOException, ServletException {
                log.warn("访问被拒绝，请求路径: {}, 异常信息: {}",
                        request.getRequestURI(), accessDeniedException.getMessage());

                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);

                // Result<String> result = Result.error("权限不足，无法访问该资源");
                BaseResponse<String> result = BaseResponse.newFailResponse(RetCodeEnum.FAILED.getErrorCode(), "权限不足，无法访问该资源");
                ObjectMapper objectMapper = new ObjectMapper();
                response.getWriter().write(objectMapper.writeValueAsString(result));
            }
        };
    }

    /**
     * 配置认证入口点
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response,
                                 AuthenticationException authException) throws IOException, ServletException {
                log.warn("认证失败，请求路径: {}, 异常类型: {}, 异常信息: {}",
                        request.getRequestURI(), authException.getClass().getSimpleName(), authException.getMessage());

                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                //Result<String> result = Result.error("认证失败，请重新登录");
                BaseResponse<String> result = BaseResponse.newFailResponse(RetCodeEnum.FAILED.getErrorCode(), "认证失败，请重新登录");
                ObjectMapper objectMapper = new ObjectMapper();
                response.getWriter().write(objectMapper.writeValueAsString(result));
            }
        };
    }
}