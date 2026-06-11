package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.entity.Dept;
import com.aircookies.smtworkordermanagement.service.DeptService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("部门控制器测试")
class DeptControllerTest {

    @Mock
    private DeptService deptService;

    @InjectMocks
    private DeptController deptController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Dept testDept;

    @BeforeEach
    @DisplayName("初始化测试环境")
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(deptController).build();
        objectMapper = new ObjectMapper();

        testDept = new Dept();
        testDept.setId(1);
        testDept.setName("研发部");
    }

    @Test
    @DisplayName("添加部门 - 路径和请求方式验证")
    void testAddDept() throws Exception {
        Result expectedResult = Result.success("添加部门成功");
        when(deptService.addDept(any(Dept.class))).thenReturn(expectedResult);

        mockMvc.perform(post("/department/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDept)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("添加部门成功"));

        verify(deptService).addDept(any(Dept.class));
    }

    @Test
    @DisplayName("根据ID删除部门")
    void testDeleteDept() throws Exception {
        Result expectedResult = Result.success("删除部门成功");
        when(deptService.deleteDept(1)).thenReturn(expectedResult);

        mockMvc.perform(delete("/department/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(deptService).deleteDept(1);
    }

    @Test
    @DisplayName("条件查询部门 - POST方式")
    void testFindDept() throws Exception {
        Dept queryDept = new Dept();
        queryDept.setName("研发部");

        Result expectedResult = Result.success(Arrays.asList(testDept));
        when(deptService.findDept(any(Dept.class))).thenReturn(expectedResult);

        mockMvc.perform(post("/department/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryDept)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(deptService).findDept(any(Dept.class));
    }

    @Test
    @DisplayName("更新部门")
    void testUpdateDept() throws Exception {
        testDept.setName("研发二部");
        Result expectedResult = Result.success("更新部门成功");
        when(deptService.updateDept(any(Dept.class))).thenReturn(expectedResult);

        mockMvc.perform(put("/department/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDept)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(deptService).updateDept(any(Dept.class));
    }
}