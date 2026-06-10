package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.entity.Dept;
import com.aircookies.smtworkordermanagement.service.DeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// 部门控制器
@Slf4j
@RestController
@RequestMapping("/department")
public class DeptController {
    private final DeptService deptService;

    @Autowired
    public DeptController(DeptService deptService) {
        this.deptService = deptService;
    }

    // 添加部门
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('1')") // 角色为1的用户可以访问(1:管理员)
    public Result addDept(@RequestBody Dept dept) {
        log.debug("添加部门: {}", dept);
        return deptService.addDept(dept);
    }

    // 删除部门
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('1')") // 角色为1的用户可以访问(1:管理员)
    public Result deleteDept(@PathVariable Integer id) {
        log.debug("删除部门: {}", id);
        return deptService.deleteDept(id);
    }

    // 查询部门
    @PostMapping("/find")
    public Result findDept(@RequestBody Dept dept) {
        log.debug("查询部门列表");
        return deptService.findDept(dept);
    }

    // 修改部门
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('1')") // 角色为1的用户可以访问(1:管理员)
    public Result updateDept(@RequestBody Dept dept) {
        log.debug("修改部门: {}", dept);
        return deptService.updateDept(dept);
    }
}
