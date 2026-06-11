package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.util.RSAUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("公钥控制器测试")
class PublicKeyControllerTest {

    @Mock
    private RSAUtil rsaUtil;

    @InjectMocks
    private PublicKeyController publicKeyController;

    private MockMvc mockMvc;

    @BeforeEach
    @DisplayName("初始化测试环境")
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(publicKeyController).build();
    }

    // ==================== 获取公钥测试 ====================

    @Test
    @DisplayName("获取公钥成功")
    void testGetPublicKey() throws Exception {
        String publicKey = "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG...\n-----END PUBLIC KEY-----";
        when(rsaUtil.getPublicKey()).thenReturn(publicKey);

        mockMvc.perform(get("/publickey"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(publicKey));

        verify(rsaUtil).getPublicKey();
    }
}