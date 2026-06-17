package com.aircookies.smtworkordermanagement.service;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.WorkOrderDetailedDTO;
import com.aircookies.smtworkordermanagement.entity.WorkOrder;
import com.aircookies.smtworkordermanagement.entity.WorkProcessReport;

import java.time.LocalDate;
import java.util.List;

/**
 * 工单服务接口（核心业务服务）
 * <p>
 * 定义工单全生命周期管理的核心业务操作，包括：
 * </p>
 * <ul>
 *   <li>工单 CRUD：添加、删除、修改、查询工单</li>
 *   <li>工序报工：对各工序进行合格/不良数量上报</li>
 *   <li>数据统计：工单详情统计、生产质量（良品/不良）统计</li>
 * </ul>
 * <p>
 * 查询结果使用 Redis 缓存，过期时间 10 分钟。
 * </p>
 */
public interface WorkOrderService {

    /**
     * 添加工单
     */
    Result addWorkOrder(WorkOrder workOrder);

    /**
     * 根据ID删除工单
     */
    Result deleteWorkOrder(List<Long> ids);

    /**
     * 更新工单
     */
    Result updateWorkOrder(WorkOrder workOrder);

    /**
     * 根据ID查询工单
     */
    Result findWorkOrderById(Long id);

    /**
     * 分页查询所有工单
     */
    Result findPage(int pageNum, int pageSize);

    /**
     * 条件查询工单
     */
    Result queryWorkOrder(WorkOrderDetailedDTO workOrderDetailedDTO);

    /**
     * 添加工序报工表
     */
    Result addWorkProcessReport(WorkProcessReport workProcessReport);

    /**
     * 根据工单ID删除工序报工表
     */
    Result deleteWorkProcessReport(Long Id);

    /**
     * 更新工序报工表
     */
    Result updateWorkProcessReport(WorkProcessReport workProcessReport);

    /**
     * 根据工单ID查询工序报工表
     */
    Result findWorkProcessReport(Long orderId);

    /**
     * 查询所有工序报工表
     */
    Result findWorkProcessReportAll(int pageNum, int pageSize);

    /**
     * 工单详细统计
     */
    Result workOrderDetailed(LocalDate startTime, LocalDate endTime);

    /**
     * 查询指定时间内的良品数和不良数统计
     */
    Result statisticsProductionQuality(LocalDate startTime, LocalDate endTime);
}