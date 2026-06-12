package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.entity.SysRole;
import com.aircookies.smtworkordermanagement.service.SysRoleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("角色控制器测试")
class SysRoleControllerTest {

    @Mock
    private SysRoleService sysRoleService;

    @InjectMocks
    private SysRoleController sysRoleController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private SysRole testRole;

    @BeforeEach
    @DisplayName("初始化测试环境")
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sysRoleController).build();
        objectMapper = new ObjectMapper();

        testRole = new SysRole();
        testRole.setId(1);
        testRole.setName("管理员");
        testRole.setDescription("系统管理员");
    }

    // ==================== 添加角色测试 ====================

    @Test
    @DisplayName("添加角色 - 路径和请求方式验证")
    void testAddRole() throws Exception {
        Result expectedResult = Result.success("添加角色成功");
        when(sysRoleService.addRole(any(SysRole.class))).thenReturn(expectedResult);

        mockMvc.perform(post("/sysRole/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRole)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(sysRoleService).addRole(any(SysRole.class));
    }

    // ==================== 更新角色测试 ====================

    @Test
    @DisplayName("更新角色")
    void testUpdateRole() throws Exception {
        testRole.setDescription("超级管理员");
        Result expectedResult = Result.success("更新角色成功");
        when(sysRoleService.updateRole(any(SysRole.class))).thenReturn(expectedResult);

        mockMvc.perform(put("/sysRole/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRole)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(sysRoleService).updateRole(any(SysRole.class));
    }

    // ==================== 删除角色测试 ====================

    @Test
    @DisplayName("根据ID删除角色")
    void testDeleteRole() throws Exception {
        Result expectedResult = Result.success("删除角色成功");
        when(sysRoleService.deleteRole(1)).thenReturn(expectedResult);

        mockMvc.perform(delete("/sysRole/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(sysRoleService).deleteRole(1);
    }

    // ==================== 根据ID查询角色测试 ====================

    @Test
    @DisplayName("根据ID查询角色")
    void testFindRole() throws Exception {
        Result expectedResult = Result.success(testRole);
        when(sysRoleService.findById(1)).thenReturn(expectedResult);

        mockMvc.perform(get("/sysRole/find/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(sysRoleService).findById(1);
    }

    // ==================== 查询所有角色测试 ====================

    @Test
    @DisplayName("查询所有角色")
    void testListRoles() throws Exception {
        Result expectedResult = Result.success(Collections.singletonList(testRole));
        when(sysRoleService.findAll()).thenReturn(expectedResult);

        mockMvc.perform(get("/sysRole/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(sysRoleService).findAll();
    }

    // ==================== 条件查询角色测试 ====================

    @Test
    @DisplayName("条件查询角色 - GET方式带RequestBody")
    void testSearchRoles() throws Exception {
        Result expectedResult = Result.success(Collections.singletonList(testRole));
        when(sysRoleService.findRoles(any(SysRole.class))).thenReturn(expectedResult);

        mockMvc.perform(get("/sysRole/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRole)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(sysRoleService).findRoles(any(SysRole.class));
    }
}