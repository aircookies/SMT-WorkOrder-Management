package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.LoginDTO;
import com.aircookies.smtworkordermanagement.dto.LoginResponseDTO;
import com.aircookies.smtworkordermanagement.service.LoginService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("登录控制器测试")
class LoginControllerTest {

    @Mock
    private LoginService loginService;

    @InjectMocks
    private LoginController loginController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    @DisplayName("初始化测试环境")
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
        objectMapper = new ObjectMapper();

        ReflectionTestUtils.setField(loginController, "jwtExpiration", 86400000L);
    }

    // ==================== 登录成功测试 ====================

    @Test
    @DisplayName("登录成功 - token正确设置到Cookie且从响应体中移除")
    void testLoginSuccess() throws Exception {
        LoginDTO loginDTO = new LoginDTO("zhangsan", "password123");

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setToken("test-jwt-token-xxxxx");
        loginResponseDTO.setUserId(1L);
        loginResponseDTO.setUsername("zhangsan");
        loginResponseDTO.setName("张三");
        loginResponseDTO.setRoleId("1");
        loginResponseDTO.setRoleName("管理员");

        Result loginResult = Result.success(loginResponseDTO);
        when(loginService.login(any(LoginDTO.class))).thenReturn(loginResult);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").doesNotExist())
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("zhangsan"))
                .andExpect(jsonPath("$.data.name").value("张三"))
                .andExpect(jsonPath("$.data.roleId").value("1"))
                .andExpect(jsonPath("$.data.roleName").value("管理员"))
                .andExpect(header().exists("Set-Cookie"));

        verify(loginService).login(any(LoginDTO.class));
    }

    // ==================== 登录失败测试 ====================

    @Test
    @DisplayName("登录失败 - 认证失败不设置Cookie")
    void testLoginFailure() throws Exception {
        LoginDTO loginDTO = new LoginDTO("zhangsan", "wrongpassword");

        Result loginResult = Result.error("用户名或密码错误");
        when(loginService.login(any(LoginDTO.class))).thenReturn(loginResult);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));

        verify(loginService).login(any(LoginDTO.class));
    }
}