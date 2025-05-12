package com.bookchain.security;

import io.jsonwebtoken.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT令牌工具类，处理令牌生成、解析和验证
 */
@Component
public class JwtTokenUtil {
    private static final String SECRET_KEY = "bookchain-secret-key-32bytes-required-for-hs512"; // 建议从配置文件读取
    private static final int TOKEN_EXPIRATION = 86400000; // 令牌有效期（24小时，单位：毫秒）

    /**
     * 从令牌中提取用户名
     */
    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 从令牌中提取过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 通用claim提取方法
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = parseClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 解析JWT令牌
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (MalformedJwtException e) {
            throw new IllegalArgumentException("无效的JWT令牌", e);
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("JWT令牌已过期", e);
        } catch (UnsupportedJwtException e) {
            throw new IllegalArgumentException("不支持的JWT令牌", e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JWT令牌参数缺失", e);
        }
    }

    /**
     * 验证令牌是否有效
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * 检查令牌是否过期
     */
    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 生成JWT令牌
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    /**
     * 生成令牌核心逻辑
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }
}
