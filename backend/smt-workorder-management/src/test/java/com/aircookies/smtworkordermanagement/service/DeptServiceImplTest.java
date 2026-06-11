package com.aircookies.smtworkordermanagement.service;

import com.aircookies.smtworkordermanagement.common.BusinessException;
import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.entity.Dept;
import com.aircookies.smtworkordermanagement.mapper.DeptMapper;
import com.aircookies.smtworkordermanagement.service.impl.DeptServiceImpl;
import com.aircookies.smtworkordermanagement.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("部门服务测试")
public class DeptServiceImplTest {

    @Mock
    private DeptMapper deptMapper;

    @Mock
    private RedisUtil redisUtil;

    @InjectMocks
    private DeptServiceImpl deptService;

    private Dept testDept;

    @BeforeEach
    @DisplayName("初始化测试数据")
    void setUp() {
        testDept = new Dept();
        testDept.setId(1);
        testDept.setName("研发部");
    }

    // ==================== 添加部门测试 ====================

    @Test
    @DisplayName("添加部门成功")
    void testAddDeptSuccess() {
        when(deptMapper.findDeptByName("研发部")).thenReturn(null);
        when(deptMapper.addDept(any(Dept.class))).thenReturn(1);

        Result result = deptService.addDept(testDept);

        assertEquals(200, result.getCode());
        assertEquals("添加部门成功", result.getMessage());
        assertNotNull(testDept.getCreateTime(), "应自动设置创建时间");
        assertNotNull(testDept.getUpdateTime(), "应自动设置更新时间");
    }

    @Test
    @DisplayName("添加部门失败 - 部门名称为空")
    void testAddDeptWithNullName() {
        testDept.setName(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> deptService.addDept(testDept));
        assertEquals("部门名称不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("添加部门失败 - 部门已存在")
    void testAddDeptAlreadyExists() {
        when(deptMapper.findDeptByName("研发部")).thenReturn(testDept);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> deptService.addDept(testDept));
        assertEquals("该部门已存在", exception.getMessage());

        verify(deptMapper, never()).addDept(any());
    }

    @Test
    @DisplayName("添加部门失败 - 数据库插入返回0")
    void testAddDeptInsertFailure() {
        when(deptMapper.findDeptByName("研发部")).thenReturn(null);
        when(deptMapper.addDept(any(Dept.class))).thenReturn(0);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> deptService.addDept(testDept));
        assertEquals("添加部门失败", exception.getMessage());
    }

    // ==================== 删除部门测试 ====================

    @Test
    @DisplayName("删除部门成功")
    void testDeleteDeptSuccess() {
        doNothing().when(deptMapper).deleteDept(1);

        Result result = deptService.deleteDept(1);

        assertEquals(200, result.getCode());
        assertEquals("删除部门成功", result.getMessage());
        verify(deptMapper).deleteDept(1);
    }

    // ==================== 查询部门测试 ====================

    @Test
    @DisplayName("查询部门成功")
    void testFindDept() {
        List<Dept> depts = Arrays.asList(testDept);
        when(deptMapper.findDept(any(Dept.class))).thenReturn(depts);

        Result result = deptService.findDept(testDept);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertInstanceOf(List.class, result.getData());
    }

    // ==================== 更新部门测试 ====================

    @Test
    @DisplayName("更新部门成功")
    void testUpdateDeptSuccess() {
        when(deptMapper.findDeptByName("研发部")).thenReturn(null);
        doNothing().when(deptMapper).updateDept(any(Dept.class));

        Result result = deptService.updateDept(testDept);

        assertEquals(200, result.getCode());
        assertEquals("更新部门成功", result.getMessage());
        assertNotNull(testDept.getUpdateTime(), "应自动设置更新时间");
    }

    @Test
    @DisplayName("更新部门失败 - 部门名称为空")
    void testUpdateDeptWithNullName() {
        testDept.setName(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> deptService.updateDept(testDept));
        assertEquals("部门名称不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("更新部门失败 - 部门名称已存在")
    void testUpdateDeptNameExists() {
        when(deptMapper.findDeptByName("研发部")).thenReturn(testDept);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> deptService.updateDept(testDept));
        assertEquals("该部门已存在", exception.getMessage());

        verify(deptMapper, never()).updateDept(any());
    }
}
