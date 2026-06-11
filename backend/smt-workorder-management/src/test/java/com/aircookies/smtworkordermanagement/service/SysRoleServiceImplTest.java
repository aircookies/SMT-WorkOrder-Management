package com.aircookies.smtworkordermanagement.service;

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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
        doNothing().when(sysRoleMapper).addRole(any(SysRole.class));

        Result result = sysRoleService.addRole(testRole);

        assertEquals(200, result.getCode());
        assertEquals("添加角色成功", result.getMessage());
        assertNotNull(testRole.getCreateTime(), "应自动设置创建时间");
        assertNotNull(testRole.getUpdateTime(), "应自动设置更新时间");
        verify(sysRoleMapper).addRole(testRole);
    }

    // ==================== 更新角色测试 ====================

    @Test
    @DisplayName("更新角色成功")
    void testUpdateRoleSuccess() {
        doNothing().when(sysRoleMapper).updateRole(any(SysRole.class));

        Result result = sysRoleService.updateRole(testRole);

        assertEquals(200, result.getCode());
        assertEquals("更新角色成功", result.getMessage());
        assertNotNull(testRole.getUpdateTime(), "应自动设置更新时间");
        verify(sysRoleMapper).updateRole(testRole);
    }

    // ==================== 删除角色测试 ====================

    @Test
    @DisplayName("删除角色成功")
    void testDeleteRoleSuccess() {
        doNothing().when(sysRoleMapper).deleteRole(1);

        Result result = sysRoleService.deleteRole(1);

        assertEquals(200, result.getCode());
        assertEquals("删除角色成功", result.getMessage());
        verify(sysRoleMapper).deleteRole(1);
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
    @DisplayName("根据ID查询不存在的角色 - 返回null数据")
    void testFindByIdNotFound() {
        when(sysRoleMapper.findById(999)).thenReturn(null);

        Result result = sysRoleService.findById(999);

        assertEquals(200, result.getCode());
        assertNull(result.getData());
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
}
