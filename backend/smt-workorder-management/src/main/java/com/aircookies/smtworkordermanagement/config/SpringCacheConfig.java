package com.aircookies.smtworkordermanagement.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring Cache 缓存配置类
 * <p>
 * 基于 Redis 实现方法级别的缓存，通过 @Cacheable、@CacheEvict 等注解简化缓存操作。
 * 使用 FastJson 序列化缓存值，String 序列化缓存键。
 * </p>
 *
 * <p>缓存策略（各业务模块的过期时间）：</p>
 * <ul>
 *   <li>部门（dept）：24小时</li>
 *   <li>产线（line）：24小时</li>
 *   <li>产品（product）：12小时</li>
 *   <li>角色（role）：24小时</li>
 *   <li>用户（user）：30分钟</li>
 *   <li>工单（workorder）：10分钟</li>
 *   <li>统计（stats）：1小时</li>
 *   <li>默认：1小时</li>
 * </ul>
 */
@Configuration
@EnableCaching
public class SpringCacheConfig {

    /**
     * 配置缓存管理器
     * <p>
     * 为每个缓存区域设置不同的 TTL，同时禁用空值缓存以避免缓存穿透。
     * </p>
     *
     * @param connectionFactory Redis 连接工厂
     * @return 配置好的 CacheManager 实例
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 使用 FastJson 序列化值，String 序列化键
        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // 默认缓存配置：1小时过期，禁用空值缓存
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringRedisSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(fastJsonRedisSerializer))
                .disableCachingNullValues();

        // 各业务模块的个性化缓存过期时间
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        cacheConfigurations.put("dept", defaultConfig.entryTtl(Duration.ofHours(24)));
        cacheConfigurations.put("line", defaultConfig.entryTtl(Duration.ofHours(24)));
        cacheConfigurations.put("product", defaultConfig.entryTtl(Duration.ofHours(12)));
        cacheConfigurations.put("role", defaultConfig.entryTtl(Duration.ofHours(24)));
        cacheConfigurations.put("user", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("workorder", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        cacheConfigurations.put("stats", defaultConfig.entryTtl(Duration.ofHours(1)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}