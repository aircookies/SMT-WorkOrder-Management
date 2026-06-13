package com.aircookies.smtworkordermanagement.service;

import com.aircookies.smtworkordermanagement.common.BusinessException;
import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.entity.SysRole;
import com.aircookies.smtworkordermanagement.mapper.SysRoleMapper;
import com.aircookies.smtworkordermanagement.service.impl.SysRoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("角色服务测试")
public class SysRoleServiceImplTest {

    @Mock
    private SysRoleMapper sysRoleMapper;

    @InjectMocks
    private SysRoleServiceImpl sysRoleService;

    private SysRole testRole;

    @BeforeEach
    @DisplayName("初始化测试数据")
    void setUp() {
        testRole = new SysRole();
        testRole.setId(1);
        testRole.setName("管理员");
        testRole.setDescription("系统管理员角色");
    }

    // ==================== 添加角色测试 ====================

    @Test
    @DisplayName("添加角色成功")
    void testAddRoleSuccess() {
        when(sysRoleMapper.addRole(any(SysRole.class))).thenReturn(1);

        Result result = sysRoleService.addRole(testRole);

        assertEquals(200, result.getCode());
        assertEquals("添加角色成功", result.getMessage());
        assertNotNull(testRole.getCreateTime(), "应自动设置创建时间");
        assertNotNull(testRole.getUpdateTime(), "应自动设置更新时间");
        verify(sysRoleMapper).addRole(testRole);
    }

    @Test
    @DisplayName("添加角色失败 - 数据库插入返回0")
    void testAddRoleFailure() {
        when(sysRoleMapper.addRole(any(SysRole.class))).thenReturn(0);

        Result result = sysRoleService.addRole(testRole);

        assertEquals(500, result.getCode());
        assertEquals("添加角色失败", result.getMessage());
    }

    // ==================== 更新角色测试 ====================

    @Test
    @DisplayName("更新角色成功")
    void testUpdateRoleSuccess() {
        when(sysRoleMapper.findById(1)).thenReturn(testRole);
        when(sysRoleMapper.updateRole(any(SysRole.class))).thenReturn(1);

        Result result = sysRoleService.updateRole(testRole);

        assertEquals(200, result.getCode());
        assertEquals("更新角色成功", result.getMessage());
        assertNotNull(testRole.getUpdateTime(), "应自动设置更新时间");
        verify(sysRoleMapper).updateRole(testRole);
    }

    @Test
    @DisplayName("更新角色失败 - 角色不存在")
    void testUpdateRoleNotFound() {
        when(sysRoleMapper.findById(1)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> sysRoleService.updateRole(testRole));
        assertEquals("该角色不存在", exception.getMessage());

        verify(sysRoleMapper, never()).updateRole(any());
    }

    @Test
    @DisplayName("更新角色失败 - 数据库更新返回0")
    void testUpdateRoleFailure() {
        when(sysRoleMapper.findById(1)).thenReturn(testRole);
        when(sysRoleMapper.updateRole(any(SysRole.class))).thenReturn(0);

        Result result = sysRoleService.updateRole(testRole);

        assertEquals(500, result.getCode());
        assertEquals("更新角色失败", result.getMessage());
    }

    // ==================== 删除角色测试 ====================

    @Test
    @DisplayName("删除角色成功")
    void testDeleteRoleSuccess() {
        when(sysRoleMapper.deleteRole(1)).thenReturn(1);

        Result result = sysRoleService.deleteRole(1);

        assertEquals(200, result.getCode());
        assertEquals("删除角色成功", result.getMessage());
        verify(sysRoleMapper).deleteRole(1);
    }

    @Test
    @DisplayName("删除角色失败 - 数据库删除返回0")
    void testDeleteRoleFailure() {
        when(sysRoleMapper.deleteRole(999)).thenReturn(0);

        Result result = sysRoleService.deleteRole(999);

        assertEquals(500, result.getCode());
        assertEquals("删除角色失败", result.getMessage());
    }

    // ==================== 查询角色测试 ====================

    @Test
    @DisplayName("根据ID查询角色成功")
    void testFindById() {
        when(sysRoleMapper.findById(1)).thenReturn(testRole);

        Result result = sysRoleService.findById(1);

        assertEquals(200, result.getCode());
        assertEquals(testRole, result.getData());
    }

    @Test
    @DisplayName("根据ID查询不存在的角色 - 返回错误信息")
    void testFindByIdNotFound() {
        when(sysRoleMapper.findById(999)).thenReturn(null);

        Result result = sysRoleService.findById(999);

        assertEquals(500, result.getCode());
        assertEquals("角色不存在", result.getMessage());
    }

    @Test
    @DisplayName("查询所有角色成功")
    void testFindAll() {
        List<SysRole> roles = Arrays.asList(
                new SysRole(1, "管理员", "系统管理员", null, null),
                new SysRole(2, "操作员", "车间操作员", null, null)
        );
        when(sysRoleMapper.findAll()).thenReturn(roles);

        Result result = sysRoleService.findAll();

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertInstanceOf(List.class, result.getData());
        assertEquals(2, ((List<?>) result.getData()).size());
    }

    @Test
    @DisplayName("查询所有角色失败 - 列表为空")
    void testFindAllEmpty() {
        when(sysRoleMapper.findAll()).thenReturn(Collections.emptyList());

        Result result = sysRoleService.findAll();

        assertEquals(500, result.getCode());
        assertEquals("角色列表为空", result.getMessage());
    }

    @Test
    @DisplayName("条件查询角色成功")
    void testFindRoles() {
        SysRole query = new SysRole();
        query.setName("管理员");

        List<SysRole> roles = List.of(testRole);
        when(sysRoleMapper.findRoles(query)).thenReturn(roles);

        Result result = sysRoleService.findRoles(query);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertInstanceOf(List.class, result.getData());
        assertEquals(1, ((List<?>) result.getData()).size());
    }

    @Test
    @DisplayName("条件查询角色失败 - 列表为空")
    void testFindRolesEmpty() {
        SysRole query = new SysRole();
        query.setName("不存在的角色");

        when(sysRoleMapper.findRoles(query)).thenReturn(Collections.emptyList());

        Result result = sysRoleService.findRoles(query);

        assertEquals(500, result.getCode());
        assertEquals("角色列表为空", result.getMessage());
    }
}