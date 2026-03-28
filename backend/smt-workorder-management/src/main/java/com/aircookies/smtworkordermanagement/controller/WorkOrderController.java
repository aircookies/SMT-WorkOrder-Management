package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.entity.WorkOrder;
import com.aircookies.smtworkordermanagement.service.WorkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 工单管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/workorder")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    @Autowired
    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    /**
     * 添加工单
     */
    @PostMapping("/add")
    public Result addWorkOrder(@RequestBody WorkOrder workOrder) {
        log.debug("添加工单: {}", workOrder);
        return workOrderService.addWorkOrder(workOrder);
    }

    /**
     * 根据ID删除工单
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteWorkOrder(@PathVariable Long id) {
        log.debug("删除工单: {}", id);
        return workOrderService.deleteWorkOrder(id);
    }

    /**
     * 更新工单
     */
    @PutMapping("/update")
    public Result updateWorkOrder(@RequestBody WorkOrder workOrder) {
        log.debug("更新工单: {}", workOrder);
        return workOrderService.updateWorkOrder(workOrder);
    }

    /**
     * 根据ID查询工单
     */
    @GetMapping("/find/{id}")
    public Result findWorkOrderById(@PathVariable Long id) {
        log.debug("查询工单: {}", id);
        return workOrderService.findWorkOrderById(id);
    }

    /**
     * 分页查询所有工单
     */
    @GetMapping("/findPage")
    public Result findPage(@RequestParam int pageNum, @RequestParam int pageSize) {
        log.debug("分页查询所有工单: {}, {}", pageNum, pageSize);
        return workOrderService.findPage(pageNum, pageSize);
    }
}
