package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.entity.Line;
import com.aircookies.smtworkordermanagement.service.LineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 产线管理控制器
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
    public Result addLine(@RequestBody Line line) {
        log.debug("添加产线: {}", line);
        return lineService.addLine(line);
    }

    /**
     * 根据ID删除产线
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteLine(@PathVariable Long id) {
        log.debug("删除产线: {}", id);
        return lineService.deleteLine(id);
    }

    /**
     * 更新产线
     */
    @PutMapping("/update")
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
}
