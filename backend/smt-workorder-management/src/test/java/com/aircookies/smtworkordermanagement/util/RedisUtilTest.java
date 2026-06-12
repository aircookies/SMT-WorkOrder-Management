package com.aircookies.smtworkordermanagement.util;

import com.alibaba.fastjson.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Redis工具类测试")
public class RedisUtilTest {

    @Autowired
    private RedisUtil redisUtil;

    private static final String TEST_KEY = "test:key";
    private static final String TEST_HASH_KEY = "test:hash:key";
    private static final String TEST_LIST_KEY = "test:list:key";
    private static final String TEST_SET_KEY = "test:set:key";
    private static final String TEST_ZSET_KEY = "test:zset:key";

    @BeforeEach
    @DisplayName("清理测试数据")
    public void setUp() {
        redisUtil.delete(TEST_KEY);
        redisUtil.delete(TEST_HASH_KEY);
        redisUtil.delete(TEST_LIST_KEY);
        redisUtil.delete(TEST_SET_KEY);
        redisUtil.delete(TEST_ZSET_KEY);
    }

    @Test
    @DisplayName("测试String类型 - 设置和获取值")
    public void testSetAndGet() {
        String value = "Hello Redis";
        redisUtil.set(TEST_KEY, value);
        Object result = redisUtil.get(TEST_KEY);
        assertEquals(value, result);
    }

    @Test
    @DisplayName("测试String类型 - 设置带过期时间的值")
    public void testSetWithExpiration() throws InterruptedException {
        String value = "Hello with expiration";
        redisUtil.set(TEST_KEY, value, 2, TimeUnit.SECONDS);

        Object result = redisUtil.get(TEST_KEY);
        assertEquals(value, result);

        Thread.sleep(3000);
        Object expiredResult = redisUtil.get(TEST_KEY);
        assertNull(expiredResult);
    }

    @Test
    @DisplayName("测试String类型 - 获取并转换为指定类型")
    public void testGetWithType() {
        TestUser user = new TestUser("张三", 25);
        redisUtil.set(TEST_KEY, user);

        TestUser result = redisUtil.get(TEST_KEY, TestUser.class);
        assertNotNull(result);
        assertEquals("张三", result.getName());
        assertEquals(25, result.getAge());
    }

    @Test
    @DisplayName("测试String类型 - 获取并转换为List类型")
    public void testGetWithListType() {
        List<TestUser> userList = Arrays.asList(
            new TestUser("张三", 25),
            new TestUser("李四", 30)
        );
        redisUtil.set(TEST_KEY, userList);

        List<TestUser> result = redisUtil.get(TEST_KEY, new TypeReference<>() {
        });
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("张三", result.get(0).getName());
        assertEquals("李四", result.get(1).getName());
    }

    @Test
    @DisplayName("测试删除键")
    public void testDelete() {
        redisUtil.set(TEST_KEY, "test value");
        assertTrue(redisUtil.hasKey(TEST_KEY));

        boolean deleted = redisUtil.delete(TEST_KEY);
        assertTrue(deleted);
        assertFalse(redisUtil.hasKey(TEST_KEY));
    }

    @Test
    @DisplayName("测试判断键是否存在")
    public void testHasKey() {
        assertFalse(redisUtil.hasKey(TEST_KEY));

        redisUtil.set(TEST_KEY, "test value");
        assertTrue(redisUtil.hasKey(TEST_KEY));
    }

    @Test
    @DisplayName("测试设置过期时间")
    public void testExpire() throws InterruptedException {
        redisUtil.set(TEST_KEY, "test value");
        redisUtil.expire(TEST_KEY, 2, TimeUnit.SECONDS);

        Long expire = redisUtil.getExpire(TEST_KEY);
        assertNotNull(expire);
        assertTrue(expire > 0 && expire <= 2);

        Thread.sleep(3000);
        assertNull(redisUtil.get(TEST_KEY));
    }

    @Test
    @DisplayName("测试获取过期时间")
    public void testGetExpire() {
        redisUtil.set(TEST_KEY, "test value", 10, TimeUnit.SECONDS);

        Long expire = redisUtil.getExpire(TEST_KEY);
        assertNotNull(expire);
        assertTrue(expire > 0 && expire <= 10);
    }

    @Test
    @DisplayName("测试Hash类型 - 设置和获取字段值")
    public void testHSetAndHGet() {
        redisUtil.hSet(TEST_HASH_KEY, "field1", "value1");
        redisUtil.hSet(TEST_HASH_KEY, "field2", "value2");

        Object result1 = redisUtil.hGet(TEST_HASH_KEY, "field1");
        Object result2 = redisUtil.hGet(TEST_HASH_KEY, "field2");

        assertEquals("value1", result1);
        assertEquals("value2", result2);
    }

    @Test
    @DisplayName("测试Hash类型 - 获取字段值并转换类型")
    public void testHGetWithType() {
        TestUser user = new TestUser("王五", 28);
        redisUtil.hSet(TEST_HASH_KEY, "user", user);

        TestUser result = redisUtil.hGet(TEST_HASH_KEY, "user", TestUser.class);
        assertNotNull(result);
        assertEquals("王五", result.getName());
        assertEquals(28, result.getAge());
    }

    @Test
    @DisplayName("测试Hash类型 - 获取所有字段和值")
    public void testHGetAll() {
        redisUtil.hSet(TEST_HASH_KEY, "field1", "value1");
        redisUtil.hSet(TEST_HASH_KEY, "field2", "value2");
        redisUtil.hSet(TEST_HASH_KEY, "field3", "value3");

        Map<Object, Object> allEntries = redisUtil.hGetAll(TEST_HASH_KEY);
        assertEquals(3, allEntries.size());
        assertEquals("value1", allEntries.get("field1"));
        assertEquals("value2", allEntries.get("field2"));
        assertEquals("value3", allEntries.get("field3"));
    }

    @Test
    @DisplayName("测试List类型 - 左侧插入和右侧弹出")
    public void testLPushAndRPop() {
        redisUtil.lPush(TEST_LIST_KEY, "item1");
        redisUtil.lPush(TEST_LIST_KEY, "item2");
        redisUtil.lPush(TEST_LIST_KEY, "item3");

        Object poppedItem = redisUtil.rPop(TEST_LIST_KEY);
        assertEquals("item1", poppedItem);
    }

    @Test
    @DisplayName("测试List类型 - 获取指定范围的元素")
    public void testLRange() {
        redisUtil.lPush(TEST_LIST_KEY, "item1");
        redisUtil.lPush(TEST_LIST_KEY, "item2");
        redisUtil.lPush(TEST_LIST_KEY, "item3");

        List<Object> range = redisUtil.lRange(TEST_LIST_KEY, 0, -1);
        assertEquals(3, range.size());
        assertEquals("item3", range.get(0));
        assertEquals("item2", range.get(1));
        assertEquals("item1", range.get(2));
    }

    @Test
    @DisplayName("测试Set类型 - 添加成员和获取所有成员")
    public void testSAddAndSMembers() {
        redisUtil.sAdd(TEST_SET_KEY, "member1");
        redisUtil.sAdd(TEST_SET_KEY, "member2");
        redisUtil.sAdd(TEST_SET_KEY, "member3");

        Set<Object> members = redisUtil.sMembers(TEST_SET_KEY);
        assertEquals(3, members.size());
        assertTrue(members.contains("member1"));
        assertTrue(members.contains("member2"));
        assertTrue(members.contains("member3"));
    }

    @Test
    @DisplayName("测试ZSet类型 - 添加元素和获取排名范围")
    public void testZAddAndZRange() {
        redisUtil.zAdd(TEST_ZSET_KEY, "item1", 1.0);
        redisUtil.zAdd(TEST_ZSET_KEY, "item2", 2.0);
        redisUtil.zAdd(TEST_ZSET_KEY, "item3", 3.0);

        Set<Object> range = redisUtil.zRange(TEST_ZSET_KEY, 0, -1);
        assertEquals(3, range.size());

        Iterator<Object> iterator = range.iterator();
        assertEquals("item1", iterator.next());
        assertEquals("item2", iterator.next());
        assertEquals("item3", iterator.next());
    }

    @Test
    @DisplayName("测试边界情况 - 获取不存在的键")
    public void testGetNonExistentKey() {
        Object result = redisUtil.get("non:existent:key");
        assertNull(result);
    }

    @Test
    @DisplayName("测试边界情况 - 删除不存在的键")
    public void testDeleteNonExistentKey() {
        boolean deleted = redisUtil.delete("non:existent:key");
        assertFalse(deleted);
    }

    @Test
    @DisplayName("测试边界情况 - Hash获取不存在的字段")
    public void testHGetNonExistentField() {
        Object result = redisUtil.hGet(TEST_HASH_KEY, "non:existent:field");
        assertNull(result);
    }

    /**
     * 测试用的用户实体类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class TestUser {
        private String name;
        private int age;
    }
}
