package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.entity.WorkOrder;
import com.aircookies.smtworkordermanagement.entity.WorkProcessReport;
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

    /*
    * 生产报工功能
    *
    * 创建工序表
    * */

    /**
     * 添加工序报工表
     */
    @PostMapping("/process/add")
    public Result addWorkProcessReport(@RequestBody WorkProcessReport workProcessReport) {
        log.debug("添加工序: {}", workProcessReport);
        return workOrderService.addWorkProcessReport(workProcessReport);
    }

    /**
     * 根据工单ID删除工序报工表
     */
    @DeleteMapping("/process/delete/{orderId}")
    public Result deleteWorkProcessReport(@PathVariable Long orderId) {
        log.debug("删除工序: orderId={}", orderId);
        return workOrderService.deleteWorkProcessReport(orderId);
    }

    /**
     * 更新工序报工表
     */
    @PutMapping("/process/update")
    public Result updateWorkProcessReport(@RequestBody WorkProcessReport workProcessReport) {
        log.debug("更新工序: {}", workProcessReport);
        return workOrderService.updateWorkProcessReport(workProcessReport);
    }

    /**
     * 根据工单ID查询工序报工表
     */
    @GetMapping("/process/find/{orderId}")
    public Result findWorkProcessReport(@PathVariable Long orderId) {
        log.debug("查询工序: orderId={}", orderId);
        return workOrderService.findWorkProcessReport(orderId);
    }
}
