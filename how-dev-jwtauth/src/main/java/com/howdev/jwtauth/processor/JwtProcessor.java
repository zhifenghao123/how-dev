package com.howdev.jwtauth.processor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtProcessor {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    /**
     * 生成AccessToken
     */
    public String generateAccessToken(String username) {
        log.debug("开始生成AccessToken，用户名: {}", username);
        return generateToken(username, accessTokenExpiration, "access");
    }

    /**
     * 生成RefreshToken
     */
    public String generateRefreshToken(String username) {
        log.debug("开始生成RefreshToken，用户名: {}", username);
        return generateToken(username, refreshTokenExpiration, "refresh");
    }

    /**
     * 生成Token
     */
    private String generateToken(String username, Long expiration, String tokenType) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("type", tokenType);

        log.debug("生成Token参数 - 用户名: {}, Token类型: {}, 过期时间: {}ms", username, tokenType, expiration);
        
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
        
        log.debug("Token生成成功，Token长度: {}", token.length());
        return token;
    }

    /**
     * 从Token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            log.debug("开始从Token中提取用户名，Token长度: {}", token.length());
            Claims claims = parseJwtClaimsFromToken(token);
            String username = claims.getSubject();
            log.debug("从Token中提取用户名成功: {}", username);
            return username;
        } catch (Exception e) {
            log.error("从Token中获取用户名失败，Token长度: {}, 异常信息: {}", token.length(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token) {
        try {
            log.debug("开始验证Token有效性，Token长度: {}", token.length());
            Claims claims = parseJwtClaimsFromToken(token);
            boolean isValid = claims.getExpiration().after(new Date());
            log.debug("Token验证结果: {}, 过期时间: {}", isValid, claims.getExpiration());
            return isValid;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Token验证失败: {}, Token长度: {}", e.getMessage(), token.length());
            return false;
        }
    }

    /**
     * 获取Token类型
     */
    public String getTokenType(String token) {
        try {
            Claims claims = parseJwtClaimsFromToken(token);
            String tokenType = (String) claims.get("type");
            log.debug("从Token中提取类型: {}", tokenType);
            return tokenType;
        } catch (Exception e) {
            log.error("获取Token类型失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从Token中解析Claims
     */
    private Claims parseJwtClaimsFromToken(String token) {
        log.debug("开始解析Token Claims，Token长度: {}", token.length());
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        log.debug("Token Claims解析成功，用户名: {}, 过期时间: {}", claims.getSubject(), claims.getExpiration());
        return claims;
    }

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        // 确保密钥长度足够（HS512需要至少512位/64字节）
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        
        // 如果密钥长度不足，使用密钥派生函数扩展
        if (keyBytes.length < 64) {
            log.debug("原始密钥长度不足，进行密钥扩展，原始长度: {}", keyBytes.length);
            // 使用SHA-512哈希来扩展密钥到64字节
            byte[] extendedKey = new byte[64];
            System.arraycopy(keyBytes, 0, extendedKey, 0, Math.min(keyBytes.length, 64));
            
            // 如果原始密钥长度不足64字节，用哈希值填充剩余部分
            java.security.MessageDigest md;
            try {
                md = java.security.MessageDigest.getInstance("SHA-512");
                byte[] hash = md.digest(keyBytes);
                // 将哈希值填充到扩展密钥的剩余部分
                System.arraycopy(hash, 0, extendedKey, keyBytes.length, 64 - keyBytes.length);
                log.debug("使用SHA-512扩展密钥到64字节");
            } catch (java.security.NoSuchAlgorithmException e) {
                // 如果SHA-512不可用，使用简单的填充
                log.debug("SHA-512不可用，使用简单填充扩展密钥");
                for (int i = keyBytes.length; i < 64; i++) {
                    extendedKey[i] = (byte) i;
                }
            }
            return Keys.hmacShaKeyFor(extendedKey);
        }
        
        log.debug("使用原始密钥，密钥长度: {}", keyBytes.length);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}