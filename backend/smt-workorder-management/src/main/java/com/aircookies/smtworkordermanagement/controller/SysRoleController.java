package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.entity.SysRole;
import com.aircookies.smtworkordermanagement.service.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/sysRole")
public class SysRoleController {
    private final SysRoleService sysRoleService;

    @Autowired
    public SysRoleController(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    // 添加角色
    @PostMapping("/add")
    public Result addRole(@RequestBody SysRole sysRole) {
        log.debug("添加角色: {}", sysRole);
        return sysRoleService.addRole(sysRole);
    }

    // 更新角色
    @PutMapping("/update")
    public Result updateRole(@RequestBody SysRole sysRole) {
        log.debug("更新角色: {}", sysRole);
        return sysRoleService.updateRole(sysRole);
    }

    // 删除角色
    @DeleteMapping("/delete/{id}")
    public Result deleteRole(@PathVariable Integer id) {
        log.debug("删除角色: {}", id);
        return sysRoleService.deleteRole(id);
    }

    // 根据ID查询角色
    @GetMapping("/find/{id}")
    public Result findRole(@PathVariable Integer id) {
        log.debug("查询角色: {}", id);
        return sysRoleService.findById(id);
    }

    // 查询所有角色
    @GetMapping("/list")
    public Result listRoles() {
        log.debug("查询所有角色");
        return sysRoleService.findAll();
    }

    // 条件查询角色
    @GetMapping("/search")
    public Result searchRoles(@RequestBody SysRole sysRole) {
        log.debug("条件查询角色: {}", sysRole);
        return sysRoleService.findRoles(sysRole);
    }
}