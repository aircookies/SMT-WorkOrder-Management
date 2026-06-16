package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.entity.WorkOrder;
import com.aircookies.smtworkordermanagement.entity.WorkProcessReport;
import com.aircookies.smtworkordermanagement.service.WorkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 工单管理控制器（核心业务控制器）
 * <p>
 * 提供工单全生命周期管理接口，包括：
 * </p>
 * <ul>
 *   <li>工单 CRUD：添加、删除、修改、查询工单</li>
 *   <li>工序报工：对工单各工序（印刷、贴片、回流焊）进行报工记录</li>
 *   <li>数据统计：工单详细信息统计、良品/不良品统计</li>
 * </ul>
 *
 * <p>权限控制：</p>
 * <ul>
 *   <li>管理员（角色ID=1）和生产计划员（角色ID=2）：可操作工单和报工</li>
 *   <li>车间操作员（角色ID=3）：仅可操作工序报工</li>
 * </ul>
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
    @PreAuthorize("hasAnyRole('1', '2')") // 角色为1和3的用户可以访问(1:管理员 2:生产计划员)
    public Result addWorkOrder(@RequestBody WorkOrder workOrder) {
        log.debug("添加工单: {}", workOrder);
        return workOrderService.addWorkOrder(workOrder);
    }

    /**
     * 根据ID删除工单
     */
    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('1', '2')") // 角色为1和3的用户可以访问(1:管理员 2:生产计划员)
    public Result deleteWorkOrder(@RequestBody List<Long> ids) {
        log.debug("删除工单: {}", ids);
        return workOrderService.deleteWorkOrder(ids);
    }

    /**
     * 更新工单
     */
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('1', '2')") // 角色为1和3的用户可以访问(1:管理员 2:生产计划员)
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
    @GetMapping("/findAll")
    public Result findPage(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
        log.debug("分页查询所有工单: {}, {}", pageNum, pageSize);
        return workOrderService.findPage(pageNum, pageSize);
    }

    /**
     * 条件查询工单
     */
    @GetMapping("/query")
    public Result queryWorkOrder(@RequestParam(defaultValue = "1") int pageNum,
                                 @RequestParam(defaultValue = "10") int pageSize,
                                 WorkOrder workOrder) {
        log.debug("条件查询工单: pageNum={}, pageSize={}, {}", pageNum, pageSize, workOrder);
        return workOrderService.queryWorkOrder(pageNum, pageSize, workOrder);
    }

    /**
     * 统计指定时间内工单的详细信息
     */
    @GetMapping("/detailed")
    public Result WorkOrderDetailed(@RequestParam LocalDate startTime, @RequestParam LocalDate endTime) {
        log.debug("统计指定时间内工单的详细信息: {} - {}", startTime, endTime);
        return workOrderService.workOrderDetailed(startTime, endTime);
    }



    // 报工相关

    /**
     * 添加工序报工表
     */
    @PostMapping("/process/add")
    @PreAuthorize("hasAnyRole('1', '2', '3')") // 角色为1,2,3的用户可以访问(1:管理员 2:生产计划员 3:车间操作员)
    public Result addWorkProcessReport(@RequestBody WorkProcessReport workProcessReport) {
        log.debug("添加工序: {}", workProcessReport);
        return workOrderService.addWorkProcessReport(workProcessReport);
    }

    /**
     * 根据报工单ID删除工序报工表
     */
    @DeleteMapping("/process/delete/{Id}")
    @PreAuthorize("hasAnyRole('1', '2', '3')") // 角色为1,2,3的用户可以访问(1:管理员 2:生产计划员 3:车间操作员)
    public Result deleteWorkProcessReport(@PathVariable Long Id) {
        log.debug("删除工序: Id={}", Id);
        return workOrderService.deleteWorkProcessReport(Id);
    }

    /**
     * 更新工序报工表
     */
    @PutMapping("/process/update")
    @PreAuthorize("hasAnyRole('1', '2', '3')") // 角色为1,2,3的用户可以访问(1:管理员 2:生产计划员 3:车间操作员)
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

    /**
     * 查询所有工序报工表
     */
    @GetMapping("/process/findAll")
    public Result findWorkProcessReportAll(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
        log.debug("查询所有工序: pageNum={}, pageSize={}", pageNum, pageSize);
        return workOrderService.findWorkProcessReportAll(pageNum, pageSize);
    }

    /**
     * 查询指定时间内的良品数和不良数统计
     */
    @GetMapping("/process/statistics")
    public Result statisticsProductionQuality(@RequestParam LocalDate startTime, @RequestParam LocalDate endTime) {
        log.debug("查询指定时间内的良品数和不良数统计: {} - {}", startTime, endTime);
        return workOrderService.statisticsProductionQuality(startTime, endTime);
    }
}