package com.aircookies.smtworkordermanagement.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置类
 * 用于配置RedisTemplate，设置键值序列化器
 */
@Configuration
public class RedisConfig {

    /**
     * 配置RedisTemplate Bean
     * 使用StringRedisSerializer作为Key的序列化器，FastJsonRedisSerializer作为Value的序列化器
     *
     * @param factory Redis连接工厂，由Spring容器自动注入
     * @return 配置好的RedisTemplate实例，支持String类型的Key和Object类型的Value
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setValueSerializer(fastJsonRedisSerializer);
        template.setHashValueSerializer(fastJsonRedisSerializer);
        template.afterPropertiesSet();

        return template;
    }
}
