package com.aircookies.smtworkordermanagement.service;

import com.aircookies.smtworkordermanagement.common.BusinessException;
import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.PagesDTO;
import com.aircookies.smtworkordermanagement.dto.QueryUserDTO;
import com.aircookies.smtworkordermanagement.entity.SysUser;
import com.aircookies.smtworkordermanagement.mapper.SysUserMapper;
import com.aircookies.smtworkordermanagement.service.impl.UserServiceImpl;
import com.aircookies.smtworkordermanagement.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务测试")
public class UserServiceImplTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RedisUtil redisUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private SysUser testUser;

    @BeforeEach
    @DisplayName("初始化测试数据")
    void setUp() {
        testUser = new SysUser();
        testUser.setId(1L);
        testUser.setUsername("zhangsan");
        testUser.setPassword("password123");
        testUser.setName("张三");
        testUser.setGender(1);
        testUser.setRoleId(1L);
        testUser.setRoleName("管理员");
        testUser.setDepartmentId(1L);
        testUser.setStatus(1);
    }

    // ==================== 添加用户测试 ====================

    @Test
    @DisplayName("添加用户成功 - 指定密码")
    void testAddUserWithPassword() {
        when(sysUserMapper.findUserByUserName("zhangsan")).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encoded");
        when(sysUserMapper.addUser(any(SysUser.class))).thenReturn(1);

        Result result = userService.addUser(testUser);

        assertEquals(200, result.getCode());
        verify(passwordEncoder).encode("password123");
        verify(sysUserMapper).addUser(testUser);
    }

    @Test
    @DisplayName("添加用户成功 - 空密码使用默认密码123456")
    void testAddUserWithEmptyPassword() {
        testUser.setPassword("");
        when(sysUserMapper.findUserByUserName("zhangsan")).thenReturn(null);
        when(passwordEncoder.encode("123456")).thenReturn("$2a$10$defaultEncoded");
        when(sysUserMapper.addUser(any(SysUser.class))).thenReturn(1);

        Result result = userService.addUser(testUser);

        assertEquals(200, result.getCode());
        verify(passwordEncoder).encode("123456");
    }

    @Test
    @DisplayName("添加用户失败 - 用户名已存在")
    void testAddUserWithDuplicateUsername() {
        when(sysUserMapper.findUserByUserName("zhangsan")).thenReturn(testUser);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.addUser(testUser));
        assertEquals("用户名已存在", exception.getMessage());

        verify(sysUserMapper, never()).addUser(any());
    }

    @Test
    @DisplayName("添加用户失败 - 数据库插入返回0")
    void testAddUserInsertFailure() {
        when(sysUserMapper.findUserByUserName("zhangsan")).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(sysUserMapper.addUser(any(SysUser.class))).thenReturn(0);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.addUser(testUser));
        assertEquals("添加用户失败", exception.getMessage());
    }

    // ==================== 删除用户测试 ====================

    @Test
    @DisplayName("删除用户成功")
    void testDeleteUserSuccess() {
        List<Long> ids = Arrays.asList(1L, 2L);
        when(sysUserMapper.deleteUser(ids)).thenReturn(2);

        Result result = userService.deleteUser(ids);

        assertEquals(200, result.getCode());
        verify(sysUserMapper).deleteUser(ids);
    }

    // ==================== 查询用户测试 ====================

    @Test
    @DisplayName("根据ID查询用户成功")
    void testFindUserById() {
        when(sysUserMapper.findById(1L)).thenReturn(testUser);

        Result result = userService.findUser(1L);

        assertEquals(200, result.getCode());
        assertEquals(testUser, result.getData());
    }

    @Test
    @DisplayName("根据ID查询不存在的用户 - 返回null数据")
    void testFindUserByIdNotFound() {
        when(sysUserMapper.findById(999L)).thenReturn(null);

        Result result = userService.findUser(999L);

        assertEquals(200, result.getCode());
        assertNull(result.getData());
    }

    // ==================== 更新用户测试 ====================

    @Test
    @DisplayName("更新用户成功")
    void testUpdateUserSuccess() {
        when(sysUserMapper.findById(1L)).thenReturn(testUser);
        when(sysUserMapper.updateUser(any(SysUser.class))).thenReturn(1);

        Result result = userService.updateUser(testUser);

        assertEquals(200, result.getCode());
        assertNotNull(testUser.getUpdateTime(), "更新用户时应设置updateTime");
        verify(sysUserMapper).updateUser(testUser);
    }

    @Test
    @DisplayName("更新用户失败 - 数据库更新返回0")
    void testUpdateUserFailure() {
        when(sysUserMapper.findById(1L)).thenReturn(testUser);
        when(sysUserMapper.updateUser(any(SysUser.class))).thenReturn(0);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.updateUser(testUser));
        assertEquals("修改用户失败", exception.getMessage());
    }

    @Test
    @DisplayName("更新用户失败 - 用户不存在")
    void testUpdateUserNotFound() {
        testUser.setId(999L);
        when(sysUserMapper.findById(999L)).thenReturn(null);
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.updateUser(testUser));

        assertEquals("用户不存在", exception.getMessage());
    }

    // ==================== 分页查询测试 ====================

    @Test
    @DisplayName("分页查询所有用户成功")
    void testFindAll() {
        List<SysUser> users = Collections.singletonList(testUser);
        when(sysUserMapper.findAll()).thenReturn(users);

        Result result = userService.findAll(1, 10);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertInstanceOf(PagesDTO.class, result.getData());
    }

    @Test
    @DisplayName("条件查询用户列表成功")
    void testUserList() {
        QueryUserDTO queryDTO = new QueryUserDTO();
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        List<SysUser> users = Collections.singletonList(testUser);
        when(sysUserMapper.UserList(queryDTO)).thenReturn(users);

        Result result = userService.UserList(queryDTO);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertInstanceOf(PagesDTO.class, result.getData());
    }
}