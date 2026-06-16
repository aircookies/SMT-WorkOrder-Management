package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.entity.SysRole;
import com.aircookies.smtworkordermanagement.service.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 角色管理控制器
 * <p>
 * 提供系统角色 CRUD 操作接口，用于管理用户权限体系。
 * 关键操作（添加、删除、修改）需要管理员权限（角色ID=1）。
 * 角色用于控制不同用户的功能访问权限（如管理员、生产计划员、车间操作员）。
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/sysRole")
public class SysRoleController {
    private final SysRoleService sysRoleService;

    @Autowired
    public SysRoleController(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    /**
     * 添加角色
     * <p>
     * 需要管理员权限。
     * </p>
     *
     * @param sysRole 角色信息
     * @return 添加结果
     */
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('1')") // 角色为1的用户可以访问(1:管理员)
    public Result addRole(@RequestBody SysRole sysRole) {
        log.debug("添加角色: {}", sysRole);
        return sysRoleService.addRole(sysRole);
    }

    /**
     * 更新角色
     * <p>
     * 需要管理员权限。根据角色 ID 更新角色信息。
     * </p>
     *
     * @param sysRole 更新后的角色信息
     * @return 更新结果
     */
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('1')") // 角色为1的用户可以访问(1:管理员)
    public Result updateRole(@RequestBody SysRole sysRole) {
        log.debug("更新角色: {}", sysRole);
        return sysRoleService.updateRole(sysRole);
    }

    /**
     * 删除角色
     * <p>
     * 需要管理员权限。根据角色 ID 删除。
     * </p>
     *
     * @param id 角色 ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('1')") // 角色为1的用户可以访问(1:管理员)
    public Result deleteRole(@PathVariable Integer id) {
        log.debug("删除角色: {}", id);
        return sysRoleService.deleteRole(id);
    }

    /**
     * 根据 ID 查询角色
     *
     * @param id 角色 ID
     * @return 角色信息
     */
    @GetMapping("/find/{id}")
    public Result findRole(@PathVariable Integer id) {
        log.debug("查询角色: {}", id);
        return sysRoleService.findById(id);
    }

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    @GetMapping("/list")
    public Result listRoles() {
        log.debug("查询所有角色");
        return sysRoleService.findAll();
    }

    /**
     * 条件查询角色
     * <p>
     * 支持按角色名称等条件查询。
     * </p>
     *
     * @param sysRole 查询条件
     * @return 符合条件的角色列表
     */
    @GetMapping("/search")
    public Result searchRoles(@RequestBody SysRole sysRole) {
        log.debug("条件查询角色: {}", sysRole);
        return sysRoleService.findRoles(sysRole);
    }
}