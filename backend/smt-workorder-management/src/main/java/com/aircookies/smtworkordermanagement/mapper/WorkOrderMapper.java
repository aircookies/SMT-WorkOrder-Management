package com.aircookies.smtworkordermanagement.mapper;

import com.aircookies.smtworkordermanagement.entity.WorkOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 工单数据访问层
 */
@Mapper
public interface WorkOrderMapper {

    /**
     * 添加工单
     */
    int addWorkOrder(WorkOrder workOrder);

    /**
     * 根据ID删除工单
     */
    void deleteWorkOrder(Long id);

    /**
     * 更新工单
     */
    void updateWorkOrder(WorkOrder workOrder);

    /**
     * 根据ID查询工单
     */
    WorkOrder findWorkOrderById(Long id);

    /**
     * 查询所有工单
     */
    List<WorkOrder> findAll();
}
