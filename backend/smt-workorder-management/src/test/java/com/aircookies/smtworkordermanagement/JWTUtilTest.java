package com.aircookies.smtworkordermanagement;

import com.aircookies.smtworkordermanagement.util.JWTUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("JWT工具类测试")
public class JWTUtilTest {

    @Autowired
    private JWTUtil jwtUtil;

    private static final String TEST_SUBJECT = "testuser";
    private static final String TEST_USER_ID = "1001";
    private static final String TEST_ROLE = "admin";
    private static final String INVALID_TOKEN = "invalid.token.here";

    private Map<String, Object> testClaims;

    @BeforeEach
    @DisplayName("初始化测试数据")
    public void setUp() {
        testClaims = new HashMap<>();
        testClaims.put("userId", TEST_USER_ID);
        testClaims.put("role", TEST_ROLE);
    }

    @Test
    @DisplayName("获取密钥实例")
    public void testGetKey() {
        SecretKey key = jwtUtil.getKey();
        assertNotNull(key, "密钥不应为空");

        SecretKey key2 = jwtUtil.getKey();
        assertSame(key, key2, "多次调用应返回相同的密钥实例");
    }

    @Test
    @DisplayName("生成简单Token")
    public void testGenerateSimpleToken() {
        String token = jwtUtil.generateToken(TEST_SUBJECT);

        assertNotNull(token, "生成的Token不应为空");
        assertFalse(token.isEmpty(), "Token长度应大于0");

        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT应包含三个部分");
    }

    @Test
    @DisplayName("生成包含声明的Token")
    public void testGenerateTokenWithClaims() {
        String token = jwtUtil.generateToken(TEST_SUBJECT, testClaims);

        assertNotNull(token, "生成的Token不应为空");
        assertFalse(token.isEmpty(), "Token长度应大于0");
    }

    @Test
    @DisplayName("解析Token获取主题")
    public void testGetSubjectFromToken() {
        String token = jwtUtil.generateToken(TEST_SUBJECT);
        String subject = jwtUtil.getSubjectFromToken(token);

        assertEquals(TEST_SUBJECT, subject, "从Token中获取的主题应与生成时一致");
    }

    @Test
    @DisplayName("解析Token获取用户名")
    public void testGetUsernameFromToken() {
        String token = jwtUtil.generateToken(TEST_SUBJECT);
        String username = jwtUtil.getUsernameFromToken(token);

        assertEquals(TEST_SUBJECT, username, "从Token中获取的用户名应与生成时一致");
    }

    @Test
    @DisplayName("解析Token获取用户ID")
    public void testGetUserIdFromToken() {
        String token = jwtUtil.generateToken(TEST_SUBJECT, testClaims);
        String userId = jwtUtil.getUserIdFromToken(token);

        assertNotNull(userId, "用户ID不应为空");
    }

    @Test
    @DisplayName("解析Token获取额外声明")
    public void testParseTokenWithClaims() {
        String token = jwtUtil.generateToken(TEST_SUBJECT, testClaims);
        Claims claims = jwtUtil.parseToken(token);

        assertNotNull(claims, "解析出的声明不应为空");
        assertEquals(TEST_SUBJECT, claims.getSubject(), "主题应匹配");
        assertEquals(TEST_USER_ID, claims.get("userId").toString(), "用户ID应匹配");
        assertEquals(TEST_ROLE, claims.get("role"), "角色应匹配");
    }

    @Test
    @DisplayName("获取Token过期时间")
    public void testGetExpirationFromToken() {
        String token = jwtUtil.generateToken(TEST_SUBJECT);
        Date expiration = jwtUtil.getExpirationFromToken(token);

        assertNotNull(expiration, "过期时间不应为空");
        assertTrue(expiration.after(new Date()), "过期时间应在当前时间之后");
    }

    @Test
    @DisplayName("检查Token未过期")
    public void testTokenNotExpired() {
        String token = jwtUtil.generateToken(TEST_SUBJECT);
        boolean isExpired = jwtUtil.isTokenExpired(token);

        assertFalse(isExpired, "新生成的Token不应过期");
    }

    @Test
    @DisplayName("验证有效Token")
    public void testValidateValidToken() {
        String token = jwtUtil.generateToken(TEST_SUBJECT);
        boolean isValid = jwtUtil.validateToken(token);

        assertTrue(isValid, "有效的Token应通过验证");
    }

    @Test
    @DisplayName("验证包含声明的Token")
    public void testValidateTokenWithClaims() {
        String token = jwtUtil.generateToken(TEST_SUBJECT, testClaims);
        boolean isValid = jwtUtil.validateToken(token);

        assertTrue(isValid, "包含声明的有效Token应通过验证");
    }

    @Test
    @DisplayName("验证无效Token")
    public void testValidateInvalidToken() {
        boolean isValid = jwtUtil.validateToken(INVALID_TOKEN);

        assertFalse(isValid, "无效的Token应验证失败");
    }

    @Test
    @DisplayName("解析无效Token应抛出异常")
    public void testParseInvalidToken() {
        assertThrows(Exception.class, () -> jwtUtil.parseToken(INVALID_TOKEN), "解析无效Token应抛出异常");
    }

    @Test
    @DisplayName("不同主题生成不同Token")
    public void testDifferentSubjectsGenerateDifferentTokens() {
        String token1 = jwtUtil.generateToken("user1");
        String token2 = jwtUtil.generateToken("user2");

        assertNotEquals(token1, token2, "不同主题应生成不同的Token");
    }

    @Test
    @DisplayName("相同主题生成不同Token")
    public void testSameSubjectGeneratesDifferentTokens() {
        String token1 = jwtUtil.generateToken(TEST_SUBJECT);
        // 延时3秒
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String token2 = jwtUtil.generateToken(TEST_SUBJECT);

        assertNotEquals(token1, token2, "相同主题在不同时间生成的Token应不同（因为签发时间不同）");
    }

    @Test
    @DisplayName("Token完整性验证")
    public void testTokenIntegrity() {
        String token = jwtUtil.generateToken(TEST_SUBJECT, testClaims);

        Claims claims = jwtUtil.parseToken(token);
        assertNotNull(claims.getIssuedAt(), "应包含签发时间");
        assertNotNull(claims.getExpiration(), "应包含过期时间");
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()),
                "过期时间应在签发时间之后");
    }
}
