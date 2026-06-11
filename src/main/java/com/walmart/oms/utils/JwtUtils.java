package com.walmart.oms.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JWT工具类
 */
@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire}")
    private long expire;

    @Value("${jwt.refresh-expire}")
    private long refreshExpire;

    // 令牌黑名单，用于存储已注销的令牌
    private static final Map<String, Date> tokenBlacklist = new ConcurrentHashMap<>();

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成JWT令牌
     */
    public String generateToken(Long userId) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expire);

        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + refreshExpire);

        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 解析JWT令牌
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从令牌中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 验证令牌是否有效
     */
    public boolean validateToken(String token) {
        try {
            // 检查令牌是否在黑名单中
            if (isTokenInBlacklist(token)) {
                log.warn("Token is in blacklist");
                return false;
            }

            // 检查令牌是否过期
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                log.warn("Token is expired");
                return false;
            }

            return true;
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 将令牌加入黑名单
     */
    public void addTokenToBlacklist(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            tokenBlacklist.put(token, expiration);
            log.info("Token added to blacklist");
        } catch (Exception e) {
            log.error("Failed to add token to blacklist: {}", e.getMessage());
        }
    }

    /**
     * 检查令牌是否在黑名单中
     */
    public boolean isTokenInBlacklist(String token) {
        // 清理过期的黑名单记录
        cleanExpiredBlacklistTokens();
        return tokenBlacklist.containsKey(token);
    }

    /**
     * 清理过期的黑名单记录
     */
    private void cleanExpiredBlacklistTokens() {
        Date now = new Date();
        tokenBlacklist.entrySet().removeIf(entry -> entry.getValue().before(now));
    }

    /**
     * 获取令牌剩余过期时间（毫秒）
     */
    public long getTokenRemainingTime(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            return expiration.getTime() - new Date().getTime();
        } catch (Exception e) {
            return -1;
        }
    }
}
