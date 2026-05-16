package com.aircookies.smtworkordermanagement.mapper;

import com.aircookies.smtworkordermanagement.dto.StatisticsGoodsDTO;
import com.aircookies.smtworkordermanagement.dto.StatisticsWorkOrderStatusDTO;
import com.aircookies.smtworkordermanagement.entity.WorkOrder;
import com.aircookies.smtworkordermanagement.entity.WorkProcessReport;
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
    void deleteWorkOrder(List<Long> ids);

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

    /**
     * 条件查询工单
     */
    List<WorkOrder> queryWorkOrder(WorkOrder workOrder);


    // ==================== 工序报工相关方法 ====================

    /**
     * 添加工序报工表
     */
    int addWorkProcessReport(WorkProcessReport workProcessReport);

    /**
     * 根据工单ID删除工序报工表
     */
    int deleteWorkProcessReport(Long orderId);

    /**
     * 更新工序报工表
     */
    int updateWorkProcessReport(WorkProcessReport workProcessReport);

    /**
     * 根据工单ID查询工序报工表
     */
    WorkProcessReport findWorkProcessReport(Long orderId);

    /**
     * 根据工单ID和工序序号查询工序报工表
     */
    int findByIdAndSeq(Long orderId, Integer processSeq);

    /**
     * 查询所有工序报工表
     */
    List<WorkProcessReport> findWorkProcessReportAll();

    /**
     * 工单数量统计
     */
    Long countWorkOrder(String status);

    /**
    * 统计所有计划生产数量，合格数量，不良数量
    * */
    List<StatisticsGoodsDTO> countGoods();

    /**
     * 分别统计各个状态的工单的数量
    * */
    List<StatisticsWorkOrderStatusDTO> countStatus();
}
