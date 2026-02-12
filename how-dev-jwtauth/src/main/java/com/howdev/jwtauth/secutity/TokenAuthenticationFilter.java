package com.howdev.jwtauth.secutity;


import com.howdev.jwtauth.processor.JwtProcessor;
import com.howdev.jwtauth.service.CustomUserDetailsService;
import com.howdev.jwtauth.util.HttpServletRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Token认证过滤器
 */
@Slf4j
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProcessor jwtProcessor;
    private final CustomUserDetailsService userDetailsService;
    private final RedisTemplate<String, Object> redisTemplate;

    public TokenAuthenticationFilter(JwtProcessor jwtProcessor,
                                     @Lazy CustomUserDetailsService userDetailsService,
                                     RedisTemplate<String, Object> redisTemplate) {
        this.jwtProcessor = jwtProcessor;
        this.userDetailsService = userDetailsService;
        this.redisTemplate = redisTemplate;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        log.debug("Token认证过滤器开始处理请求: {}", requestPath);

        try {
            // 获取Token
            String token = HttpServletRequestUtil.getAccessTokenFromRequest(request);

            if (StringUtils.hasText(token)) {
                log.debug("从请求中获取到Token，开始验证Token有效性");

                if (jwtProcessor.validateToken(token)) {
                    log.debug("Token验证成功，开始提取用户名");
                    // 从Token中获取用户名
                    String username = jwtProcessor.getUsernameFromToken(token);

                    if (StringUtils.hasText(username)) {
                        log.debug("从Token中提取到用户名: {}", username);

                        if (SecurityContextHolder.getContext().getAuthentication() == null) {
                            log.debug("安全上下文为空，开始检查Token类型");
                            // 检查Token类型（只处理AccessToken）
                            String tokenType = jwtProcessor.getTokenType(token);
                            log.debug("Token类型: {}", tokenType);

                            if (!"access".equals(tokenType)) {
                                log.warn("无效的Token类型: {}，跳过认证处理", tokenType);
                                filterChain.doFilter(request, response);
                                return;
                            }

                            log.debug("开始加载用户详情: {}", username);
                            // 加载用户详情
                            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                            if (userDetails != null) {
                                log.debug("用户详情加载成功，开始创建认证对象");
                                // 创建认证对象
                                UsernamePasswordAuthenticationToken authentication =
                                        new UsernamePasswordAuthenticationToken(
                                                userDetails,
                                                null,
                                                userDetails.getAuthorities()
                                        );

                                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                                // 设置到安全上下文
                                SecurityContextHolder.getContext().setAuthentication(authentication);

                                // 将用户信息存储到Request属性中（可选）
                                request.setAttribute("currentUser", userDetails);

                                log.info("设置用户认证成功: {}", username);
                            } else {
                                log.warn("用户详情加载失败: {}", username);
                            }
                        } else {
                            log.debug("安全上下文已存在认证信息，跳过认证处理");
                        }
                    } else {
                        log.warn("从Token中提取用户名失败");
                    }
                } else {
                    log.warn("Token验证失败");
                }
            } else {
                log.debug("请求中未包含Token，跳过认证处理");
            }
        } catch (Exception e) {
            log.error("Token认证过程中发生错误，请求路径: {}, 异常信息: {}", requestPath, e.getMessage(), e);
            // 清除安全上下文
            SecurityContextHolder.clearContext();
            // 继续执行过滤器链，让ExceptionTranslationFilter处理认证异常
        }

        log.debug("Token认证过滤器处理完成，继续执行过滤器链");
        filterChain.doFilter(request, response);
    }
}