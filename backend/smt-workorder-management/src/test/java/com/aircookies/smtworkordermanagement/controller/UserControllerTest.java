package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.QueryUserDTO;
import com.aircookies.smtworkordermanagement.entity.SysUser;
import com.aircookies.smtworkordermanagement.service.UserService;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("用户控制器测试")
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private SysUser testUser;

    @BeforeEach
    @DisplayName("初始化测试环境")
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();

        testUser = new SysUser();
        testUser.setId(1L);
        testUser.setUsername("zhangsan");
        testUser.setPassword("password123");
        testUser.setName("张三");
        testUser.setGender(1);
        testUser.setRoleId(1L);
        testUser.setDepartmentId(1L);
        testUser.setStatus(1);
    }

    @Test
    @DisplayName("添加用户 - 路径和请求方式验证")
    void testAddUser() throws Exception {
        Result expectedResult = Result.success("添加用户成功");
        when(userService.addUser(any(SysUser.class))).thenReturn(expectedResult);

        mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("添加用户成功"));

        verify(userService).addUser(any(SysUser.class));
    }

    @Test
    @DisplayName("删除用户 - 批量删除")
    void testDeleteUser() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        Result expectedResult = Result.success("删除用户成功");
        when(userService.deleteUser(ids)).thenReturn(expectedResult);

        mockMvc.perform(delete("/user/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除用户成功"));

        verify(userService).deleteUser(ids);
    }

    @Test
    @DisplayName("条件查询用户 - POST方式")
    void testUserList() throws Exception {
        QueryUserDTO queryDTO = new QueryUserDTO();
        queryDTO.setName("张三");
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Result expectedResult = Result.success(Collections.singletonList(testUser));
        when(userService.UserList(any(QueryUserDTO.class))).thenReturn(expectedResult);

        mockMvc.perform(post("/user/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(userService).UserList(any(QueryUserDTO.class));
    }

    @Test
    @DisplayName("分页查询所有用户 - 默认参数")
    void testFindAllWithDefaultParams() throws Exception {
        Result expectedResult = Result.success(Collections.singletonList(testUser));
        when(userService.findAll(1, 10)).thenReturn(expectedResult);

        mockMvc.perform(get("/user/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(userService).findAll(1, 10);
    }

    @Test
    @DisplayName("分页查询所有用户 - 自定义分页参数")
    void testFindAllWithCustomParams() throws Exception {
        Result expectedResult = Result.success(Collections.singletonList(testUser));
        when(userService.findAll(2, 20)).thenReturn(expectedResult);

        mockMvc.perform(get("/user/findAll")
                        .param("pageNum", "2")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(userService).findAll(2, 20);
    }

    @Test
    @DisplayName("根据ID查询用户")
    void testFindUser() throws Exception {
        Result expectedResult = Result.success(testUser);
        when(userService.findUser(1L)).thenReturn(expectedResult);

        mockMvc.perform(get("/user/find/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(userService).findUser(1L);
    }

    @Test
    @DisplayName("更新用户")
    void testUpdateUser() throws Exception {
        Result expectedResult = Result.success("更新用户成功");
        when(userService.updateUser(any(SysUser.class))).thenReturn(expectedResult);

        mockMvc.perform(put("/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(userService).updateUser(any(SysUser.class));
    }
}