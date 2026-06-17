package com.aircookies.smtworkordermanagement.service;

import com.aircookies.smtworkordermanagement.common.BusinessException;
import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.PagesDTO;
import com.aircookies.smtworkordermanagement.dto.ProductionQualityDTO;
import com.aircookies.smtworkordermanagement.dto.WorkOrderDetailedDTO;
import com.aircookies.smtworkordermanagement.entity.WorkOrder;
import com.aircookies.smtworkordermanagement.entity.WorkProcessReport;
import com.aircookies.smtworkordermanagement.mapper.WorkOrderMapper;
import com.aircookies.smtworkordermanagement.service.impl.WorkOrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("工单服务测试")
public class WorkOrderServiceImplTest {

    @Mock
    private WorkOrderMapper workOrderMapper;

    @InjectMocks
    private WorkOrderServiceImpl workOrderService;

    private WorkOrder testWorkOrder;
    private WorkProcessReport testReport;

    @BeforeEach
    @DisplayName("初始化测试数据")
    void setUp() {
        testWorkOrder = new WorkOrder();
        testWorkOrder.setId(1L);
        testWorkOrder.setProductId(1L);
        testWorkOrder.setLineId(1L);
        testWorkOrder.setQuantity(100);
        testWorkOrder.setStatus(0);
        testWorkOrder.setPriority(0);

        testReport = new WorkProcessReport();
        testReport.setId(1L);
        testReport.setOrderId(1L);
        testReport.setProcessSeq(1);
        testReport.setQualifiedQuantity(90);
        testReport.setBadQuantity(10);
    }

    // ==================== 工单CRUD测试 ====================

    @Test
    @DisplayName("添加工单成功")
    void testAddWorkOrderSuccess() {
        when(workOrderMapper.addWorkOrder(any(WorkOrder.class))).thenReturn(1);

        Result result = workOrderService.addWorkOrder(testWorkOrder);

        assertEquals(200, result.getCode());
        assertEquals("添加工单成功", result.getMessage());
        assertNotNull(testWorkOrder.getCreateTime(), "应自动设置创建时间");
        assertNotNull(testWorkOrder.getUpdateTime(), "应自动设置更新时间");
        verify(workOrderMapper).addWorkOrder(testWorkOrder);
    }

    @Test
    @DisplayName("添加工单失败 - 数据库插入返回0")
    void testAddWorkOrderFailure() {
        when(workOrderMapper.addWorkOrder(any(WorkOrder.class))).thenReturn(0);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> workOrderService.addWorkOrder(testWorkOrder));
        assertEquals("添加工单失败", exception.getMessage());
    }

    @Test
    @DisplayName("删除工单成功")
    void testDeleteWorkOrderSuccess() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        doNothing().when(workOrderMapper).deleteWorkOrder(ids);

        Result result = workOrderService.deleteWorkOrder(ids);

        assertEquals(200, result.getCode());
        assertEquals("删除工单成功", result.getMessage());
        verify(workOrderMapper).deleteWorkOrder(ids);
    }

    @Test
    @DisplayName("删除工单 - 空列表")
    void testDeleteWorkOrderWithEmptyList() {
        List<Long> ids = new ArrayList<>();

        Result result = workOrderService.deleteWorkOrder(ids);

        assertEquals(200, result.getCode());
        verify(workOrderMapper).deleteWorkOrder(ids);
    }

    @Test
    @DisplayName("更新工单成功")
    void testUpdateWorkOrderSuccess() {
        doNothing().when(workOrderMapper).updateWorkOrder(any(WorkOrder.class));

        Result result = workOrderService.updateWorkOrder(testWorkOrder);

        assertEquals(200, result.getCode());
        assertEquals("更新工单成功", result.getMessage());
        assertNotNull(testWorkOrder.getUpdateTime(), "应自动设置更新时间");
        verify(workOrderMapper).updateWorkOrder(testWorkOrder);
    }

    @Test
    @DisplayName("根据ID查询工单成功")
    void testFindWorkOrderById() {
        when(workOrderMapper.findWorkOrderById(1L)).thenReturn(testWorkOrder);

        Result result = workOrderService.findWorkOrderById(1L);

        assertEquals(200, result.getCode());
        assertEquals(testWorkOrder, result.getData());
    }

    @Test
    @DisplayName("根据ID查询不存在的工单 - 返回null数据")
    void testFindWorkOrderByIdNotFound() {
        when(workOrderMapper.findWorkOrderById(999L)).thenReturn(null);

        Result result = workOrderService.findWorkOrderById(999L);

        assertEquals(200, result.getCode());
        assertNull(result.getData());
    }

    // ==================== 工序报工测试 ====================

    @Test
    @DisplayName("添加工序报工成功")
    void testAddWorkProcessReportSuccess() {
        when(workOrderMapper.findByIdAndSeq(1L, 1)).thenReturn(0);
        when(workOrderMapper.addWorkProcessReport(any(WorkProcessReport.class))).thenReturn(1);

        Result result = workOrderService.addWorkProcessReport(testReport);

        assertEquals(200, result.getCode());
        assertEquals("报工成功", result.getMessage());
        assertNotNull(testReport.getCreateTime(), "应自动设置创建时间");
        assertNotNull(testReport.getUpdateTime(), "应自动设置更新时间");
        assertNotNull(testReport.getStartTime(), "应自动设置开始时间");
    }

    @Test
    @DisplayName("添加工序报工失败 - 重复报工")
    void testAddWorkProcessReportDuplicate() {
        when(workOrderMapper.findByIdAndSeq(1L, 1)).thenReturn(1);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> workOrderService.addWorkProcessReport(testReport));
        assertEquals("请勿重复报工", exception.getMessage());

        // 不应执行插入操作
        verify(workOrderMapper, never()).addWorkProcessReport(any());
    }

    @Test
    @DisplayName("添加工序报工失败 - 数据库插入返回0")
    void testAddWorkProcessReportInsertFailure() {
        when(workOrderMapper.findByIdAndSeq(1L, 1)).thenReturn(0);
        when(workOrderMapper.addWorkProcessReport(any(WorkProcessReport.class))).thenReturn(0);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> workOrderService.addWorkProcessReport(testReport));
        assertEquals("报工失败", exception.getMessage());
    }

    @Test
    @DisplayName("删除工序报工成功")
    void testDeleteWorkProcessReportSuccess() {
        when(workOrderMapper.deleteWorkProcessReport(1L)).thenReturn(1);

        Result result = workOrderService.deleteWorkProcessReport(1L);

        assertEquals(200, result.getCode());
        assertEquals("删除成功", result.getMessage());
    }

    @Test
    @DisplayName("删除工序报工失败 - 记录不存在")
    void testDeleteWorkProcessReportFailure() {
        when(workOrderMapper.deleteWorkProcessReport(999L)).thenReturn(0);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> workOrderService.deleteWorkProcessReport(999L));
        assertEquals("删除失败", exception.getMessage());
    }

    @Test
    @DisplayName("更新工序报工成功")
    void testUpdateWorkProcessReportSuccess() {
        when(workOrderMapper.findWorkProcessReport(1L)).thenReturn(Collections.singletonList(testReport));
        when(workOrderMapper.updateWorkProcessReport(any(WorkProcessReport.class))).thenReturn(1);

        Result result = workOrderService.updateWorkProcessReport(testReport);

        assertEquals(200, result.getCode());
        assertEquals("更新成功", result.getMessage());
        assertNotNull(testReport.getUpdateTime(), "应自动设置更新时间");
    }

    @Test
    @DisplayName("更新工序报工失败 - 工序报工表不存在")
    void testUpdateWorkProcessReportNotFound() {
        when(workOrderMapper.findWorkProcessReport(1L)).thenReturn(Collections.emptyList());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> workOrderService.updateWorkProcessReport(testReport));
        assertEquals("工序报工表不存在", exception.getMessage());

        verify(workOrderMapper, never()).updateWorkProcessReport(any());
    }

    @Test
    @DisplayName("更新工序报工失败 - 数据库更新返回0")
    void testUpdateWorkProcessReportFailure() {
        when(workOrderMapper.findWorkProcessReport(1L)).thenReturn(Collections.singletonList(testReport));
        when(workOrderMapper.updateWorkProcessReport(any(WorkProcessReport.class))).thenReturn(0);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> workOrderService.updateWorkProcessReport(testReport));
        assertEquals("更新失败", exception.getMessage());
    }

    // ==================== 统计查询测试 ====================

    @Test
    @DisplayName("查询指定时间内的工单详细统计")
    void testWorkOrderDetailed() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 6, 11);
        List<WorkOrderDetailedDTO> mockData = List.of();

        when(workOrderMapper.workOrderDetailed(start, end)).thenReturn(mockData);

        Result result = workOrderService.workOrderDetailed(start, end);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(workOrderMapper).workOrderDetailed(start, end);
    }

    @Test
    @DisplayName("查询指定时间内的良品数和不良数统计")
    void testStatisticsProductionQuality() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 6, 11);
        List<ProductionQualityDTO> mockData = List.of();

        when(workOrderMapper.statisticsProductionQuality(start, end)).thenReturn(mockData);

        Result result = workOrderService.statisticsProductionQuality(start, end);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(workOrderMapper).statisticsProductionQuality(start, end);
    }

    // ==================== 分页查询工单测试 ====================

    @Test
    @DisplayName("分页查询所有工单成功")
    void testFindPage() {
        List<WorkOrder> workOrders = Collections.singletonList(testWorkOrder);
        when(workOrderMapper.findAll()).thenReturn(workOrders);

        Result result = workOrderService.findPage(1, 10);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertInstanceOf(PagesDTO.class, result.getData());
    }

    @Test
    @DisplayName("条件查询工单成功")
    void testQueryWorkOrder() {
        List<WorkOrder> workOrders = Collections.singletonList(testWorkOrder);
        when(workOrderMapper.queryWorkOrder(any(WorkOrder.class))).thenReturn(workOrders);

        Result result = workOrderService.queryWorkOrder(1, 10, testWorkOrder);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertInstanceOf(PagesDTO.class, result.getData());
    }

    // ==================== 查询工序报工测试 ====================

    @Test
    @DisplayName("根据工单ID查询工序报工成功")
    void testFindWorkProcessReportSuccess() {
        List<WorkProcessReport> expectedList = Collections.singletonList(testReport);
        when(workOrderMapper.findWorkProcessReport(1L)).thenReturn(expectedList);

        Result result = workOrderService.findWorkProcessReport(1L);

        assertEquals(200, result.getCode());
        assertEquals(expectedList, result.getData());
    }

    @Test
    @DisplayName("根据工单ID查询工序报工 - 无记录时返回空列表")
    void testFindWorkProcessReportNotFound() {
        when(workOrderMapper.findWorkProcessReport(999L)).thenReturn(Collections.emptyList());

        Result result = workOrderService.findWorkProcessReport(999L);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertInstanceOf(List.class, result.getData());
        assertTrue(((List<?>) result.getData()).isEmpty(), "无报工记录时应返回空列表");
    }

    @Test
    @DisplayName("分页查询所有工序报工成功")
    void testFindWorkProcessReportAll() {
        List<WorkProcessReport> reports = Collections.singletonList(testReport);
        when(workOrderMapper.findWorkProcessReportAll()).thenReturn(reports);

        Result result = workOrderService.findWorkProcessReportAll(1, 10);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertInstanceOf(PagesDTO.class, result.getData());
    }
}