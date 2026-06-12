package com.aircookies.smtworkordermanagement.service.impl;

import com.aircookies.smtworkordermanagement.common.CacheConstants;
import com.aircookies.smtworkordermanagement.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * JWT令牌缓存服务类
 * 基于Redis实现JWT令牌的缓存管理，提供令牌的存储、查询、失效和验证功能
 */
@Service
@Slf4j
public class JwtTokenCacheService {

    private final RedisUtil redisUtil;

    /**
     * 构造函数注入RedisUtil依赖
     *
     * @param redisUtil Redis工具类实例，用于执行缓存操作
     */
    @Autowired
    public JwtTokenCacheService(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    /**
     * 将JWT令牌及其关联的用户信息缓存到Redis中
     *
     * @param token JWT令牌字符串
     * @param userInfo 与令牌关联的用户信息对象
     */
    public void cacheToken(String token, Object userInfo) {
        String key = CacheConstants.getJwtTokenCacheKey(token);
        log.info("缓存JWT令牌 {} 到Redis，过期时间为 {} 秒", token, CacheConstants.JWT_TOKEN_CACHE_EXPIRE);
        redisUtil.set(key, userInfo, CacheConstants.JWT_TOKEN_CACHE_EXPIRE, TimeUnit.SECONDS);
    }

    /**
     * 从Redis缓存中获取指定JWT令牌对应的用户信息
     *
     * @param token JWT令牌字符串
     * @return 缓存的用户信息对象，如果令牌不存在则返回null
     */
    public Object getCachedTokenInfo(String token) {
        log.info("从Redis缓存中获取JWT令牌 {} 的用户信息", token);
        String key = CacheConstants.getJwtTokenCacheKey(token);
        return redisUtil.get(key);
    }

    /**
     * 使指定的JWT令牌失效，从Redis缓存中删除该令牌及其关联信息
     *
     * @param token 需要失效的JWT令牌字符串
     */
    public void invalidateToken(String token) {
        log.info("使JWT令牌 {} 失效，从Redis缓存中删除", token);
        String key = CacheConstants.getJwtTokenCacheKey(token);
        redisUtil.delete(key);
    }

    /**
     * 验证JWT令牌是否在Redis缓存中存在且有效
     *
     * @param token JWT令牌字符串
     * @return 如果令牌非空且在缓存中存在返回true，否则返回false
     */
    public boolean isTokenValid(String token) {
        log.info("验证JWT令牌 {} 是否有效", token);
        if (token == null) {
            return false;
        }

        String key = CacheConstants.getJwtTokenCacheKey(token);
        return redisUtil.hasKey(key);
    }
}