package com.howdev.jwtauth.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class HttpServletRequestUtil {

    private static final String ACCESS_TOKEN_BEAR_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";

    private static final String REFRESH_TOKEN_HEADER_NAME = "RefreshToken";

    public static String getAccessTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER_NAME);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(ACCESS_TOKEN_BEAR_PREFIX)) {
            String token = bearerToken.substring(ACCESS_TOKEN_BEAR_PREFIX.length());
            log.debug("从Authorization头中提取Token，Token长度: {}", token.length());
            return token;
        }
        log.debug("请求头中未找到有效的Authorization信息");
        return null;
    }

    public static String getRefreshTokenFromRequest(HttpServletRequest request) {
        String refreshToken = request.getHeader(REFRESH_TOKEN_HEADER_NAME);
        if (StringUtils.hasText(refreshToken)) {
            log.debug("从header头中提取RefreshToken，RefreshToken长度: {}", refreshToken.length());
            return refreshToken;
        }
        return null;
    }
}
