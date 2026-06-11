package com.aircookies.smtworkordermanagement.service;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.LoginDTO;
import com.aircookies.smtworkordermanagement.dto.LoginResponseDTO;
import com.aircookies.smtworkordermanagement.entity.SysUser;
import com.aircookies.smtworkordermanagement.mapper.SysUserMapper;
import com.aircookies.smtworkordermanagement.service.impl.JwtTokenCacheService;
import com.aircookies.smtworkordermanagement.service.impl.LoginServiceImpl;
import com.aircookies.smtworkordermanagement.util.JWTUtil;
import com.aircookies.smtworkordermanagement.util.RSAUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("登录服务测试")
public class LoginServiceImplTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RSAUtil rsaUtil;

    @Mock
    private JwtTokenCacheService jwtTokenCacheService;

    @InjectMocks
    private LoginServiceImpl loginService;

    private SysUser testUser;

    @BeforeEach
    @DisplayName("初始化测试数据")
    void setUp() {
        testUser = new SysUser();
        testUser.setId(1L);
        testUser.setUsername("admin");
        testUser.setPassword("$2a$10$encodedPassword");
        testUser.setName("管理员");
        testUser.setRoleId(1L);
        testUser.setRoleName("管理员");
    }

    @Test
    @DisplayName("登录成功 - 正确的用户名和密码")
    void testLoginSuccess() throws Exception {
        LoginDTO loginDTO = new LoginDTO("admin", "encryptedPassword");

        when(sysUserMapper.findUserByUserName("admin")).thenReturn(testUser);
        when(rsaUtil.decrypt("encryptedPassword")).thenReturn("rawPassword");
        when(passwordEncoder.matches("rawPassword", "$2a$10$encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(eq("admin"), anyMap())).thenReturn("mock-jwt-token");

        Result result = loginService.login(loginDTO);

        // 验证返回结果
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertInstanceOf(LoginResponseDTO.class, result.getData());

        LoginResponseDTO responseDTO = (LoginResponseDTO) result.getData();
        assertEquals("mock-jwt-token", responseDTO.getToken());
        assertEquals(1L, responseDTO.getUserId());
        assertEquals("admin", responseDTO.getUsername());
        assertEquals("管理员", responseDTO.getName());
        assertEquals("1", responseDTO.getRoleId());
        assertEquals("管理员", responseDTO.getRoleName());

        // 验证缓存Token被调用
        verify(jwtTokenCacheService).cacheToken(eq("mock-jwt-token"), anyMap());
    }

    @Test
    @DisplayName("登录失败 - 参数为null")
    void testLoginWithNullDTO() {
        assertThrows(NullPointerException.class, () -> loginService.login(null));
    }

    @Test
    @DisplayName("登录失败 - 用户名为空")
    void testLoginWithEmptyUsername() {
        LoginDTO loginDTO = new LoginDTO("", "password");

        assertThrows(NullPointerException.class, () -> loginService.login(loginDTO));
    }

    @Test
    @DisplayName("登录失败 - 密码为空")
    void testLoginWithEmptyPassword() {
        LoginDTO loginDTO = new LoginDTO("admin", "");

        assertThrows(NullPointerException.class, () -> loginService.login(loginDTO));
    }

    @Test
    @DisplayName("登录失败 - 用户不存在")
    void testLoginWithNonExistentUser() {
        LoginDTO loginDTO = new LoginDTO("nonexistent", "password");

        when(sysUserMapper.findUserByUserName("nonexistent")).thenReturn(null);

        assertThrows(BadCredentialsException.class, () -> loginService.login(loginDTO));
    }

    @Test
    @DisplayName("登录失败 - RSA解密失败")
    void testLoginWithRSADecryptFailure() throws Exception {
        LoginDTO loginDTO = new LoginDTO("admin", "badEncrypted");

        when(sysUserMapper.findUserByUserName("admin")).thenReturn(testUser);
        when(rsaUtil.decrypt("badEncrypted")).thenThrow(new Exception("解密失败"));

        assertThrows(IllegalArgumentException.class, () -> loginService.login(loginDTO));
    }

    @Test
    @DisplayName("登录失败 - 密码不匹配")
    void testLoginWithWrongPassword() throws Exception {
        LoginDTO loginDTO = new LoginDTO("admin", "encryptedPassword");

        when(sysUserMapper.findUserByUserName("admin")).thenReturn(testUser);
        when(rsaUtil.decrypt("encryptedPassword")).thenReturn("wrongPassword");
        when(passwordEncoder.matches("wrongPassword", "$2a$10$encodedPassword")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> loginService.login(loginDTO));
    }
}
