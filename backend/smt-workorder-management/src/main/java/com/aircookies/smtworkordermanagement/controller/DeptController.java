package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.entity.Dept;
import com.aircookies.smtworkordermanagement.service.DeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 部门管理控制器
 * <p>
 * 提供部门 CRUD 操作接口，包括添加、删除、修改、查询部门。
 * 关键操作（添加、删除、修改）需要管理员权限（角色ID=1）。
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/department")
public class DeptController {
    private final DeptService deptService;

    @Autowired
    public DeptController(DeptService deptService) {
        this.deptService = deptService;
    }

    /**
     * 添加部门
     * <p>
     * 需要管理员权限。部门名称不能重复。
     * </p>
     *
     * @param dept 部门信息
     * @return 添加结果
     */
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('1')") // 角色为1的用户可以访问(1:管理员)
    public Result addDept(@RequestBody Dept dept) {
        log.debug("添加部门: {}", dept);
        return deptService.addDept(dept);
    }

    /**
     * 删除部门
     * <p>
     * 需要管理员权限。根据部门 ID 删除。
     * </p>
     *
     * @param id 部门 ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('1')") // 角色为1的用户可以访问(1:管理员)
    public Result deleteDept(@PathVariable Integer id) {
        log.debug("删除部门: {}", id);
        return deptService.deleteDept(id);
    }

    /**
     * 条件查询部门
     * <p>
     * 支持按部门名称等条件查询，不传条件则返回所有部门。
     * </p>
     *
     * @param dept 查询条件（可选）
     * @return 部门列表
     */
    @PostMapping("/find")
    public Result findDept(@RequestBody Dept dept) {
        log.debug("查询部门列表");
        return deptService.findDept(dept);
    }

    /**
     * 修改部门
     * <p>
     * 需要管理员权限。根据部门 ID 更新部门信息。
     * </p>
     *
     * @param dept 更新后的部门信息
     * @return 更新结果
     */
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('1')") // 角色为1的用户可以访问(1:管理员)
    public Result updateDept(@RequestBody Dept dept) {
        log.debug("修改部门: {}", dept);
        return deptService.updateDept(dept);
    }
}