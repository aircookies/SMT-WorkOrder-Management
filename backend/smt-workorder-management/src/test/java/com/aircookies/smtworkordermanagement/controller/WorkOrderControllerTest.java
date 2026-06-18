package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.WorkOrderDetailedDTO;
import com.aircookies.smtworkordermanagement.entity.WorkOrder;
import com.aircookies.smtworkordermanagement.entity.WorkProcessReport;
import com.aircookies.smtworkordermanagement.service.WorkOrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("工单控制器测试")
class WorkOrderControllerTest {

    @Mock
    private WorkOrderService workOrderService;

    @InjectMocks
    private WorkOrderController workOrderController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private WorkOrder testWorkOrder;
    private WorkOrderDetailedDTO testWorkOrderDetailedDTO;
    private WorkProcessReport testReport;

    @BeforeEach
    @DisplayName("初始化测试环境")
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(workOrderController).build();
        objectMapper = new ObjectMapper();

        testWorkOrder = new WorkOrder();
        testWorkOrder.setId(1L);
        testWorkOrder.setProductId(1L);
        testWorkOrder.setLineId(1L);
        testWorkOrder.setQuantity(100);
        testWorkOrder.setStatus(0);
        testWorkOrder.setPriority(1);

        testWorkOrderDetailedDTO = new WorkOrderDetailedDTO();
        testWorkOrderDetailedDTO.setPageNum(1);
        testWorkOrderDetailedDTO.setPageSize(10);

        testReport = new WorkProcessReport();
        testReport.setId(1L);
        testReport.setOrderId(1L);
        testReport.setProcessSeq(1);
        testReport.setQualifiedQuantity(90);
        testReport.setBadQuantity(10);
        testReport.setOperatorId(1L);
    }

    // ==================== 添加工单测试 ====================

    @Test
    @DisplayName("添加工单")
    void testAddWorkOrder() throws Exception {
        Result expectedResult = Result.success("添加工单成功");
        when(workOrderService.addWorkOrder(any(WorkOrder.class))).thenReturn(expectedResult);

        mockMvc.perform(post("/workorder/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testWorkOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(workOrderService).addWorkOrder(any(WorkOrder.class));
    }

    // ==================== 删除工单测试 ====================

    @Test
    @DisplayName("批量删除工单")
    void testDeleteWorkOrder() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L);
        Result expectedResult = Result.success("删除工单成功");
        when(workOrderService.deleteWorkOrder(ids)).thenReturn(expectedResult);

        mockMvc.perform(delete("/workorder/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(workOrderService).deleteWorkOrder(ids);
    }

    // ==================== 更新工单测试 ====================

    @Test
    @DisplayName("更新工单")
    void testUpdateWorkOrder() throws Exception {
        Result expectedResult = Result.success("更新工单成功");
        when(workOrderService.updateWorkOrder(any(WorkOrder.class))).thenReturn(expectedResult);

        mockMvc.perform(put("/workorder/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testWorkOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(workOrderService).updateWorkOrder(any(WorkOrder.class));
    }

    // ==================== 根据ID查询工单测试 ====================

    @Test
    @DisplayName("根据ID查询工单")
    void testFindWorkOrderById() throws Exception {
        Result expectedResult = Result.success(testWorkOrder);
        when(workOrderService.findWorkOrderById(1L)).thenReturn(expectedResult);

        mockMvc.perform(get("/workorder/find/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(workOrderService).findWorkOrderById(1L);
    }

    // ==================== 分页查询所有工单测试 ====================

    @Test
    @DisplayName("分页查询所有工单 - 默认参数")
    void testFindPageWithDefaultParams() throws Exception {
        Result expectedResult = Result.success(Collections.singletonList(testWorkOrder));
        when(workOrderService.findPage(1, 10)).thenReturn(expectedResult);

        mockMvc.perform(get("/workorder/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(workOrderService).findPage(1, 10);
    }

    @Test
    @DisplayName("分页查询所有工单 - 自定义参数")
    void testFindPageWithCustomParams() throws Exception {
        Result expectedResult = Result.success(Collections.singletonList(testWorkOrder));
        when(workOrderService.findPage(2, 20)).thenReturn(expectedResult);

        mockMvc.perform(get("/workorder/findAll")
                        .param("pageNum", "2")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(workOrderService).findPage(2, 20);
    }

    // ==================== 条件查询工单测试 ====================

    @Test
    @DisplayName("条件查询工单 - POST方式")
    void testQueryWorkOrder() throws Exception {
        Result expectedResult = Result.success(Collections.singletonList(testWorkOrder));
        when(workOrderService.queryWorkOrder(any(WorkOrderDetailedDTO.class))).thenReturn(expectedResult);

        mockMvc.perform(post("/workorder/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testWorkOrderDetailedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(workOrderService).queryWorkOrder(any(WorkOrderDetailedDTO.class));
    }

    // ==================== 工单详细统计测试 ====================

    @Test
    @DisplayName("工单详细统计 - 指定时间范围")
    void testWorkOrderDetailed() throws Exception {
        LocalDate startTime = LocalDate.of(2026, 6, 1);
        LocalDate endTime = LocalDate.of(2026, 6, 10);
        Result expectedResult = Result.success(List.of());

        when(workOrderService.workOrderDetailed(startTime, endTime)).thenReturn(expectedResult);

        mockMvc.perform(get("/workorder/detailed")
                        .param("startTime", "2026-06-01")
                        .param("endTime", "2026-06-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(workOrderService).workOrderDetailed(startTime, endTime);
    }

    // ==================== 报工 - 添加工序报工表测试 ====================

    @Test
    @DisplayName("添加工序报工表")
    void testAddWorkProcessReport() throws Exception {
        Result expectedResult = Result.success("添加报工成功");
        when(workOrderService.addWorkProcessReport(any(WorkProcessReport.class))).thenReturn(expectedResult);

        mockMvc.perform(post("/workorder/process/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testReport)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(workOrderService).addWorkProcessReport(any(WorkProcessReport.class));
    }

    // ==================== 报工 - 删除工序报工表测试 ====================

    @Test
    @DisplayName("根据报工单ID删除工序报工表")
    void testDeleteWorkProcessReport() throws Exception {
        Result expectedResult = Result.success("删除报工成功");
        when(workOrderService.deleteWorkProcessReport(1L)).thenReturn(expectedResult);

        mockMvc.perform(delete("/workorder/process/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(workOrderService).deleteWorkProcessReport(1L);
    }

    // ==================== 报工 - 更新工序报工表测试 ====================

    @Test
    @DisplayName("更新工序报工表")
    void testUpdateWorkProcessReport() throws Exception {
        Result expectedResult = Result.success("更新报工成功");
        when(workOrderService.updateWorkProcessReport(any(WorkProcessReport.class))).thenReturn(expectedResult);

        mockMvc.perform(put("/workorder/process/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testReport)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(workOrderService).updateWorkProcessReport(any(WorkProcessReport.class));
    }

    // ==================== 报工 - 根据工单ID查询工序报工表测试 ====================

    @Test
    @DisplayName("根据工单ID查询工序报工表")
    void testFindWorkProcessReport() throws Exception {
        Result expectedResult = Result.success(Collections.singletonList(testReport));
        when(workOrderService.findWorkProcessReport(1L)).thenReturn(expectedResult);

        mockMvc.perform(get("/workorder/process/find/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(workOrderService).findWorkProcessReport(1L);
    }

    // ==================== 报工 - 查询所有工序报工表测试 ====================

    @Test
    @DisplayName("查询所有工序报工表 - 分页")
    void testFindWorkProcessReportAll() throws Exception {
        Result expectedResult = Result.success(Collections.singletonList(testReport));
        when(workOrderService.findWorkProcessReportAll(1, 10)).thenReturn(expectedResult);

        mockMvc.perform(get("/workorder/process/findAll")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(workOrderService).findWorkProcessReportAll(1, 10);
    }

    // ==================== 报工 - 良品数和不良数统计测试 ====================

    @Test
    @DisplayName("良品数和不良数统计 - 指定时间范围")
    void testStatisticsProductionQuality() throws Exception {
        LocalDate startTime = LocalDate.of(2026, 6, 1);
        LocalDate endTime = LocalDate.of(2026, 6, 10);
        Result expectedResult = Result.success(List.of());

        when(workOrderService.statisticsProductionQuality(startTime, endTime)).thenReturn(expectedResult);

        mockMvc.perform(get("/workorder/process/statistics")
                        .param("startTime", "2026-06-01")
                        .param("endTime", "2026-06-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(workOrderService).statisticsProductionQuality(startTime, endTime);
    }
}