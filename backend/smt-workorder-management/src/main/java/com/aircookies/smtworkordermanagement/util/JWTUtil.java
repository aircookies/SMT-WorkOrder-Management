package com.aircookies.smtworkordermanagement.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * JWT工具类
 * 提供JWT令牌的生成、解析和验证功能
 */
@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    private static SecretKey key;

   /**
     * 获取签名密钥
     * 使用懒加载方式创建密钥实例，确保密钥只被创建一次
     *
     * @return SecretKey 签名密钥
     */
    public SecretKey getKey() {
        if (key == null) {
            key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        }
        return key;
    }

    /**
     * 生成JWT令牌
     *
     * @param subject 令牌主题，通常用于存储用户名或用户ID
     * @return String 生成的JWT令牌字符串
     */
    public String generateToken(String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        return Jwts.builder()
                .subject(subject)
                .id(uuid)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getKey())
                .compact();
    }

    /**
     * 生成包含额外声明的JWT令牌
     *
     * @param subject 令牌主题，通常用于存储用户名或用户ID
     * @param claims 额外的声明信息，如用户角色、权限等自定义数据
     * @return String 生成的JWT令牌字符串
     */
    public String generateToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .id(uuid)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getKey())
                .compact();
    }

    /**
     * 解析JWT令牌
     *
     * @param token JWT令牌字符串
     * @return Claims 令牌中的声明信息
     * @throws io.jsonwebtoken.JwtException 当令牌无效或已过期时抛出异常
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从令牌中获取主题信息
     *
     * @param token JWT令牌字符串
     * @return String 令牌主题
     */
    public String getSubjectFromToken(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token JWT令牌字符串
     * @return String 用户名
     */
    public String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 从令牌中获取用户ID
     *
     * @param token JWT令牌字符串
     * @return String 用户ID
     */
    public String getUserIdFromToken(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 从令牌中获取过期时间
     *
     * @param token JWT令牌字符串
     * @return Date 过期时间
     */
    public Date getExpirationFromToken(String token) {
        return parseToken(token).getExpiration();
    }

    /**
     * 检查令牌是否已过期
     *
     * @param token JWT令牌字符串
     * @return boolean 如果令牌已过期返回true，否则返回false
     */
    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 验证JWT令牌的有效性
     * 包括签名验证和过期检查
     *
     * @param token JWT令牌字符串
     * @return boolean 如果令牌有效返回true，否则返回false
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
