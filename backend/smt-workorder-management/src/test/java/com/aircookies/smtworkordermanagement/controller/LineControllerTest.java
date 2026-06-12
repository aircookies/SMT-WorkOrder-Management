package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.entity.Line;
import com.aircookies.smtworkordermanagement.service.LineService;
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
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("产线控制器测试")
class LineControllerTest {

    @Mock
    private LineService lineService;

    @InjectMocks
    private LineController lineController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Line testLine;

    @BeforeEach
    @DisplayName("初始化测试环境")
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(lineController).build();
        objectMapper = new ObjectMapper();

        testLine = new Line();
        testLine.setId(1L);
        testLine.setName("SMT产线1");
        testLine.setDescription("SMT贴片产线");
    }

    @Test
    @DisplayName("添加产线")
    void testAddLine() throws Exception {
        Result expectedResult = Result.success("添加产线成功");
        when(lineService.addLine(any(Line.class))).thenReturn(expectedResult);

        mockMvc.perform(post("/line/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLine)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(lineService).addLine(any(Line.class));
    }

    @Test
    @DisplayName("根据ID删除产线")
    void testDeleteLine() throws Exception {
        Result expectedResult = Result.success("删除产线成功");
        when(lineService.deleteLine(1L)).thenReturn(expectedResult);

        mockMvc.perform(delete("/line/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(lineService).deleteLine(1L);
    }

    @Test
    @DisplayName("更新产线")
    void testUpdateLine() throws Exception {
        Result expectedResult = Result.success("更新产线成功");
        when(lineService.updateLine(any(Line.class))).thenReturn(expectedResult);

        mockMvc.perform(put("/line/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLine)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(lineService).updateLine(any(Line.class));
    }

    @Test
    @DisplayName("根据ID查询产线")
    void testFindLineById() throws Exception {
        Result expectedResult = Result.success(testLine);
        when(lineService.findLineById(1L)).thenReturn(expectedResult);

        mockMvc.perform(get("/line/find/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(lineService).findLineById(1L);
    }

    @Test
    @DisplayName("查询所有产线")
    void testFindAll() throws Exception {
        Result expectedResult = Result.success(Collections.singletonList(testLine));
        when(lineService.findAll()).thenReturn(expectedResult);

        mockMvc.perform(get("/line/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(lineService).findAll();
    }

    @Test
    @DisplayName("产线统计 - 指定日期范围")
    void testStatistics() throws Exception {
        LocalDate startTime = LocalDate.of(2026, 6, 1);
        LocalDate endTime = LocalDate.of(2026, 6, 10);
        Result expectedResult = Result.success(List.of());

        when(lineService.statistics(startTime, endTime)).thenReturn(expectedResult);

        mockMvc.perform(get("/line/statistics")
                        .param("startTime", "2026-06-01")
                        .param("endTime", "2026-06-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(lineService).statistics(startTime, endTime);
    }
}