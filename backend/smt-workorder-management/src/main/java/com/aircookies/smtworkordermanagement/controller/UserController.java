package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.QueryUserDTO;
import com.aircookies.smtworkordermanagement.entity.SysUser;
import com.aircookies.smtworkordermanagement.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 添加用户
    @PostMapping("/add")
    public Result addUser(@RequestBody SysUser user) {
        log.debug("添加用户: {}", user);
        return userService.addUser(user);
    }

    // 根据ID删除用户
    @DeleteMapping("/delete")
    public Result deleteUser(@RequestBody List<Long> ids) {
        log.debug("删除用户: {}", ids);
        return userService.deleteUser(ids);
    }

    // 查询指定用户
    @PostMapping("/list")
    public Result UserList(@RequestBody QueryUserDTO keyword) {
        log.debug("查询指定用户: {}", keyword);
        return userService.UserList(keyword);
    }

    // 分页查询所有用户
    @GetMapping("/findAll")
    public Result findAll(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize) {
        log.debug("查询所有用户, 页码: {}, 页大小: {}", pageNum, pageSize);
        return userService.findAll(pageNum, pageSize);
    }

    // 根据ID查询用户
    @GetMapping("/find/{id}")
    public Result findUser(@PathVariable Long id) {
        log.debug("查询用户: {}", id);
        return userService.findUser(id);
    }

    // 修改用户
    @PutMapping("/update")
    public Result updateUser(@RequestBody SysUser user) {
        log.debug("修改用户: {}", user);
        return userService.updateUser(user);
    }
}
