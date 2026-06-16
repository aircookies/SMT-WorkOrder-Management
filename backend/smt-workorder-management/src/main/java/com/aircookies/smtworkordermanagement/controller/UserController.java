package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.QueryUserDTO;
import com.aircookies.smtworkordermanagement.entity.SysUser;
import com.aircookies.smtworkordermanagement.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 * <p>
 * 提供用户 CRUD 操作接口，包括添加、删除、修改、查询用户。
 * 关键操作（添加、删除、修改）需要管理员权限（角色ID=1）。
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 添加用户
     * <p>
     * 需要管理员权限。如果密码为空，系统会自动设置默认密码。
     * </p>
     *
     * @param user 用户信息
     * @return 添加结果
     */
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('1')") // 角色为1的用户可以访问(1:管理员)
    public Result addUser(@RequestBody SysUser user) {
        log.debug("添加用户: {}", user);
        return userService.addUser(user);
    }

    /**
     * 批量删除用户
     * <p>
     * 需要管理员权限。支持批量删除，传入用户 ID 列表。
     * </p>
     *
     * @param ids 用户 ID 列表
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('1')") // 角色为1的用户可以访问(1:管理员)
    public Result deleteUser(@RequestBody List<Long> ids) {
        log.debug("删除用户: {}", ids);
        return userService.deleteUser(ids);
    }

    /**
     * 条件查询用户（分页）
     * <p>
     * 支持按姓名、性别、角色、部门等条件组合查询。
     * </p>
     *
     * @param keyword 查询条件（包含分页参数）
     * @return 分页用户列表
     */
    @PostMapping("/list")
    public Result UserList(@RequestBody QueryUserDTO keyword) {
        log.debug("查询指定用户: {}", keyword);
        return userService.UserList(keyword);
    }

    /**
     * 分页查询所有用户
     *
     * @param pageNum  页码（默认1）
     * @param pageSize 每页数量（默认10）
     * @return 分页用户列表
     */
    @GetMapping("/findAll")
    public Result findAll(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize) {
        log.debug("查询所有用户, 页码: {}, 页大小: {}", pageNum, pageSize);
        return userService.findAll(pageNum, pageSize);
    }

    /**
     * 根据 ID 查询用户
     *
     * @param id 用户 ID
     * @return 用户信息
     */
    @GetMapping("/find/{id}")
    public Result findUser(@PathVariable Long id) {
        log.debug("查询用户: {}", id);
        return userService.findUser(id);
    }

    /**
     * 修改用户
     * <p>
     * 需要管理员权限。可修改用户的基本信息、角色、部门等。
     * 修改时会自动更新密码加密和修改时间。
     * </p>
     *
     * @param user 更新后的用户信息
     * @return 更新结果
     */
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('1')") // 角色为1的用户可以访问(1:管理员)
    public Result updateUser(@RequestBody SysUser user) {
        log.debug("修改用户: {}", user);
        return userService.updateUser(user);
    }
}