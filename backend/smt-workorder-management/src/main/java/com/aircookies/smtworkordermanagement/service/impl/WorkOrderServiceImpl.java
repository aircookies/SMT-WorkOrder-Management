package com.aircookies.smtworkordermanagement.service.impl;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.PagesDTO;
import com.aircookies.smtworkordermanagement.entity.WorkOrder;
import com.aircookies.smtworkordermanagement.entity.WorkProcessReport;
import com.aircookies.smtworkordermanagement.mapper.WorkOrderMapper;
import com.aircookies.smtworkordermanagement.service.WorkOrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.aircookies.smtworkordermanagement.common.Result.success;

/**
 * 工单服务实现类
 */
@Service
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
            return Result.error("添加工单失败");
        }
    }

    /**
     * 根据ID删除工单
     */
    @Override
    public Result deleteWorkOrder(Long id) {
        workOrderMapper.deleteWorkOrder(id);
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
        return success(workOrder);
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
        return success(
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
    public Result addWorkProcessReport(WorkProcessReport workProcessReport) {
        // 先检查是否该工单是否存在该工序报工记录
        int existRecord = workOrderMapper.findByIdAndSeq(workProcessReport.getOrderId(), workProcessReport.getProcessSeq());
        if (existRecord != 0) {
            return Result.error("该工序已存在报工记录");
        }
        workProcessReport.setCreateTime(LocalDateTime.now());
        workProcessReport.setUpdateTime(LocalDateTime.now());
        workProcessReport.setStartTime(LocalDateTime.now());
        int res = workOrderMapper.addWorkProcessReport(workProcessReport);
        if (res != 0) {
            return Result.success("添加工序成功");
        } else {
            return Result.error("添加工序失败");
        }
    }

    /**
     * 根据工单ID删除工序报工表
     */
    @Override
    public Result deleteWorkProcessReport(Long orderId) {
        int res = workOrderMapper.deleteWorkProcessReport(orderId);
        if (res != 0) {
            return Result.success("删除工序成功");
        } else {
            return Result.error("删除工序失败");
        }
    }

    /**
     * 更新工序报工表
     */
    @Override
    public Result updateWorkProcessReport(WorkProcessReport workProcessReport) {
        workProcessReport.setUpdateTime(LocalDateTime.now());
        int res = workOrderMapper.updateWorkProcessReport(workProcessReport);
        if (res != 0) {
            return Result.success("更新工序成功");
        } else {
            return Result.error("更新工序失败");
        }
    }

    /**
     * 根据工单ID查询工序报工表
     */
    @Override
    public Result findWorkProcessReport(Long orderId) {
        WorkProcessReport workProcessReport = workOrderMapper.findWorkProcessReport(orderId);
        return success(workProcessReport);
    }
}
