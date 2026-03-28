package com.aircookies.smtworkordermanagement.service;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.entity.WorkOrder;

/**
 * 工单服务接口
 */
public interface WorkOrderService {

    /**
     * 添加工单
     */
    Result addWorkOrder(WorkOrder workOrder);

    /**
     * 根据ID删除工单
     */
    Result deleteWorkOrder(Long id);

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
}
