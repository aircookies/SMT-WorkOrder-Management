package com.aircookies.smtworkordermanagement.service.impl;

import com.aircookies.smtworkordermanagement.common.BusinessException;
import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.PagesDTO;
import com.aircookies.smtworkordermanagement.dto.WorkOrderDetailedDTO;
import com.aircookies.smtworkordermanagement.entity.WorkOrder;
import com.aircookies.smtworkordermanagement.entity.WorkProcessReport;
import com.aircookies.smtworkordermanagement.mapper.WorkOrderMapper;
import com.aircookies.smtworkordermanagement.service.WorkOrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.aircookies.smtworkordermanagement.common.Result.success;

/**
 * 工单服务实现类（核心业务服务实现）
 * <p>
 * 实现工单全生命周期管理的核心业务逻辑，包括：
 * </p>
 * <ul>
 *   <li>工单 CRUD：添加、删除、修改、查询工单</li>
 *   <li>工序报工：对各工序（印刷、贴片、回流焊）进行合格/不良数量上报（含重复报工检查）</li>
 *   <li>数据统计：工单详情统计、良品/不良品生产质量统计</li>
 * </ul>
 * <p>
 * 查询结果通过 Spring Cache 自动缓存（过期时间 10 分钟），保证工单状态变化能较快反映。
 * </p>
 */
@Service
@Slf4j
public class WorkOrderServiceImpl implements WorkOrderService {

    private final WorkOrderMapper workOrderMapper;

    @Autowired
    public WorkOrderServiceImpl(WorkOrderMapper workOrderMapper) {
        this.workOrderMapper = workOrderMapper;
    }

    /**
     * 添加工单
     */
    @Override
    public Result addWorkOrder(WorkOrder workOrder) {
        workOrder.setCreateTime(LocalDateTime.now());
        workOrder.setUpdateTime(LocalDateTime.now());
        int res = workOrderMapper.addWorkOrder(workOrder);
        if (res != 0) {
            return success("添加工单成功");
        } else {
            log.warn("添加工单失败，工单ID：{}", workOrder.getId());
            throw new BusinessException("添加工单失败");
        }
    }

    /**
     * 根据ID删除工单
     */
    @Override
    public Result deleteWorkOrder(List<Long> ids) {
        workOrderMapper.deleteWorkOrder(ids);
        return Result.success("删除工单成功");
    }

    /**
     * 更新工单
     */
    @Override
    public Result updateWorkOrder(WorkOrder workOrder) {
        workOrder.setUpdateTime(LocalDateTime.now());
        workOrderMapper.updateWorkOrder(workOrder);
        return Result.success("更新工单成功");
    }

    /**
     * 根据ID查询工单
     */
    @Override
    public Result findWorkOrderById(Long id) {
        WorkOrder workOrder = workOrderMapper.findWorkOrderById(id);
        return Result.success(workOrder);
    }

    /**
     * 分页查询所有工单
     */
    @Override
    public Result findPage(int pageNum, int pageSize) {
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);
        List<WorkOrder> workOrders = workOrderMapper.findAll();
        // 获取分页结果
        PageInfo<WorkOrder> pageInfo = new PageInfo<>(workOrders);
        // 封装结果并返回
        return Result.success(
                new PagesDTO<>(
                        pageInfo.getPageNum(),
                        pageInfo.getPageSize(),
                        pageInfo.getTotal(),
                        pageInfo.getList()
                )
        );
    }

    /**
     * 条件查询工单
     */
    @Override
    public Result queryWorkOrder(WorkOrderDetailedDTO workOrderDetailedDTO) {
        // 开启分页
        PageHelper.startPage(workOrderDetailedDTO.getPageNum(), workOrderDetailedDTO.getPageSize());
        List<WorkOrder> workOrders = workOrderMapper.queryWorkOrder(workOrderDetailedDTO);
        // 获取分页结果
        PageInfo<WorkOrder> pageInfo = new PageInfo<>(workOrders);
        // 封装结果并返回
        return Result.success(
                new PagesDTO<>(
                        pageInfo.getPageNum(),
                        pageInfo.getPageSize(),
                        pageInfo.getTotal(),
                        pageInfo.getList()
                )
        );
    }

    /**
     * 添加工序报工表
     */
    @Override
    @Transactional
    public Result addWorkProcessReport(WorkProcessReport workProcessReport) {
        // 先检查是否该工单是否存在该工序报工记录
        int existRecord = workOrderMapper.findByIdAndSeq(workProcessReport.getOrderId(), workProcessReport.getProcessSeq());
        if (existRecord != 0) {
            throw new BusinessException("请勿重复报工");
        }

        workProcessReport.setCreateTime(LocalDateTime.now());
        workProcessReport.setUpdateTime(LocalDateTime.now());
        workProcessReport.setStartTime(LocalDateTime.now());

        // 如果是最后一个工序，则判断工单完成
        if (workProcessReport.getProcessSeq().equals(3)) {
            long orderId = workProcessReport.getOrderId();
            WorkOrder workOrder = workOrderMapper.findWorkOrderById(orderId);
            if (workOrder == null) {
                throw new BusinessException("工单不存在");
            }

            // 将工单状态设置为已完成
            workOrder.setStatus(2);
            workOrderMapper.updateWorkOrder(workOrder);
        }

        // 添加工序报工表
        int res = workOrderMapper.addWorkProcessReport(workProcessReport);
        if (res != 0) {
            return Result.success("报工成功");
        } else {
            log.warn("报工失败，工单ID：{}", workProcessReport.getOrderId());
            throw new BusinessException("报工失败");
        }
    }

    /**
     * 根据报工单ID删除工序报工表
     */
    @Override
    public Result deleteWorkProcessReport(Long Id) {
        int res = workOrderMapper.deleteWorkProcessReport(Id);
        if (res != 0) {
            return Result.success("删除成功");
        } else {
            log.warn("删除工序报工表失败，工序报工表ID：{}", Id);
            throw new BusinessException("删除失败");
        }
    }

    /**
     * 更新工序报工表
     */
    @Override
    public Result updateWorkProcessReport(WorkProcessReport workProcessReport) {
        // 检查工序报工表是否存在
        if (workProcessReport == null || workOrderMapper.findWorkProcessReport(workProcessReport.getId()) == null) {
            throw new BusinessException("工序报工表不存在");
        }

        workProcessReport.setUpdateTime(LocalDateTime.now());
        int res = workOrderMapper.updateWorkProcessReport(workProcessReport);
        if (res != 0) {
            return Result.success("更新成功");
        } else {
            log.warn("更新工序报工表失败，工序报工表ID：{}", workProcessReport.getId());
            throw new BusinessException("更新失败");
        }
    }

    /**
     * 根据工单ID查询工序报工表
     */
    @Override
    public Result findWorkProcessReport(Long orderId) {
        WorkProcessReport workProcessReport = workOrderMapper.findWorkProcessReport(orderId);
        if (workProcessReport == null) {
            log.warn("查询工序报工表失败，工单ID：{}", orderId);
            throw new BusinessException("查询数据失败");
        }
        return Result.success(workProcessReport);
    }

    @Override
    public Result findWorkProcessReportAll(int pageNum, int pageSize) {
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);
        List<WorkProcessReport> workProcessReports = workOrderMapper.findWorkProcessReportAll();
        PageInfo<WorkProcessReport> pageInfo = new PageInfo<>(workProcessReports);
        return Result.success(
                new PagesDTO<>(
                        pageInfo.getPageNum(),
                        pageInfo.getPageSize(),
                        pageInfo.getTotal(),
                        pageInfo.getList()
                )
        );
    }

    @Override
    public Result workOrderDetailed(LocalDate startTime, LocalDate endTime) {
        return Result.success(workOrderMapper.workOrderDetailed(startTime, endTime));
    }

    @Override
    public Result statisticsProductionQuality(LocalDate startTime, LocalDate endTime) {
        return Result.success(workOrderMapper.statisticsProductionQuality(startTime, endTime));
    }
}