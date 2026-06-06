package com.aircookies.smtworkordermanagement.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 * 提供常用的Redis操作封装，支持String、Hash、List、Set、ZSet等数据结构
 */
@Component
public class RedisUtil {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置键值对（无过期时间）
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置键值对并指定过期时间
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 获取键对应的值
     *
     * @param key 键
     * @return 值对象
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 获取键对应的值并转换为指定类型
     *
     * @param key   键
     * @param clazz 目标类型
     * @param <T>   泛型类型
     * @return 转换后的对象，如果键不存在则返回null
     */
    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(value), clazz);
    }

    /**
     * 获取键对应的值并转换为指定泛型列表类型
     *
     * @param key           键
     * @param typeReference 类型引用，用于指定泛型类型
     * @param <T>           泛型类型
     * @return 转换后的列表对象，如果键不存在则返回null
     */
    public <T> List<T> get(String key, TypeReference<List<T>> typeReference) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(value), typeReference);
    }

    /**
     * 删除指定的键
     *
     * @param key 键
     * @return 是否删除成功
     */
    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    /**
     * 判断键是否存在
     *
     * @param key 键
     * @return 如果键存在返回true，否则返回false
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 设置键的过期时间
     *
     * @param key     键
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return 是否设置成功
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
    }

    /**
     * 获取键的剩余过期时间
     *
     * @param key 键
     * @return 剩余过期时间（秒），如果键不存在或没有设置过期时间则返回null
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 设置Hash结构的字段和值
     *
     * @param key     键
     * @param hashKey 哈希字段
     * @param value   值
     */
    public void hSet(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 获取Hash结构中指定字段的值
     *
     * @param key     键
     * @param hashKey 哈希字段
     * @return 字段对应的值
     */
    public Object hGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 获取Hash结构中指定字段的值并转换为指定类型
     *
     * @param key     键
     * @param hashKey 哈希字段
     * @param clazz   目标类型
     * @param <T>     泛型类型
     * @return 转换后的对象，如果字段不存在则返回null
     */
    public <T> T hGet(String key, String hashKey, Class<T> clazz) {
        Object value = redisTemplate.opsForHash().get(key, hashKey);
        if (value == null) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(value), clazz);
    }

    /**
     * 获取Hash结构中所有的字段和值
     *
     * @param key 键
     * @return 包含所有字段和值的Map
     */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 向List结构左侧插入元素
     *
     * @param key   键
     * @param value 值
     */
    public void lPush(String key, Object value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 从List结构右侧弹出元素
     *
     * @param key 键
     * @return 弹出的元素，如果列表为空则返回null
     */
    public Object rPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 获取List结构中指定范围的元素
     *
     * @param key   键
     * @param start 起始索引
     * @param end   结束索引
     * @return 指定范围内的元素列表
     */
    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 向Set结构中添加元素
     *
     * @param key   键
     * @param value 值
     */
    public void sAdd(String key, Object value) {
        redisTemplate.opsForSet().add(key, value);
    }

    /**
     * 获取Set结构中所有的成员
     *
     * @param key 键
     * @return 集合中的所有成员
     */
    public Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 向ZSet结构中添加元素并指定分数
     *
     * @param key   键
     * @param value 值
     * @param score 分数，用于排序
     */
    public void zAdd(String key, Object value, double score) {
        redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 获取ZSet结构中指定排名范围的元素
     *
     * @param key   键
     * @param start 起始排名（从0开始）
     * @param end   结束排名
     * @return 指定排名范围内的元素集合
     */
    public Set<Object> zRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }
}
