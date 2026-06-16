package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.entity.Line;
import com.aircookies.smtworkordermanagement.service.LineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 产线管理控制器
 * <p>
 * 提供产线 CRUD 操作接口，包括添加、删除、修改、查询产线。
 * 关键操作（添加、删除、修改）需要管理员权限（角色ID=1）。
 * 同时提供产线产量统计功能。
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/line")
public class LineController {

    private final LineService lineService;

    @Autowired
    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    /**
     * 添加产线
     */
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('1')") // 角色为1的用户可以访问(1:管理员)
    public Result addLine(@RequestBody Line line) {
        log.debug("添加产线: {}", line);
        return lineService.addLine(line);
    }

    /**
     * 根据ID删除产线
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('1')") // 角色为1的用户可以访问(1:管理员)
    public Result deleteLine(@PathVariable Long id) {
        log.debug("删除产线: {}", id);
        return lineService.deleteLine(id);
    }

    /**
     * 更新产线
     */
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('1')") // 角色为1的用户可以访问(1:管理员)
    public Result updateLine(@RequestBody Line line) {
        log.debug("更新产线: {}", line);
        return lineService.updateLine(line);
    }

    /**
     * 根据ID查询产线
     */
    @GetMapping("/find/{id}")
    public Result findLineById(@PathVariable Long id) {
        log.debug("查询产线: {}", id);
        return lineService.findLineById(id);
    }

    /**
     * 查询所有产线
     */
    @GetMapping("/findAll")
    public Result findAll() {
        log.debug("查询所有产线");
        return lineService.findAll();
    }

    /**
     *  统计每条产线在指定日期范围内的计划数量和完成数量
     */
    @GetMapping("/statistics")
    public Result statistics(@RequestParam LocalDate startTime, @RequestParam LocalDate endTime) {
        return lineService.statistics(startTime, endTime);
    }
}