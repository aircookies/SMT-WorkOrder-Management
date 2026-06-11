package com.aircookies.smtworkordermanagement.service;

import com.aircookies.smtworkordermanagement.common.BusinessException;
import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.LineOutputDTO;
import com.aircookies.smtworkordermanagement.entity.Line;
import com.aircookies.smtworkordermanagement.mapper.LineMapper;
import com.aircookies.smtworkordermanagement.service.impl.LineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("产线服务测试")
public class LineServiceImplTest {

    @Mock
    private LineMapper lineMapper;

    @InjectMocks
    private LineServiceImpl lineService;

    private Line testLine;

    @BeforeEach
    @DisplayName("初始化测试数据")
    void setUp() {
        testLine = new Line();
        testLine.setId(1L);
        testLine.setName("SMT-产线A");
        testLine.setDescription("SMT贴片产线A");
    }

    // ==================== 添加产线测试 ====================

    @Test
    @DisplayName("添加产线成功")
    void testAddLineSuccess() {
        when(lineMapper.findLineByName("SMT-产线A")).thenReturn(null);
        when(lineMapper.addLine(any(Line.class))).thenReturn(1);

        Result result = lineService.addLine(testLine);

        assertEquals(200, result.getCode());
        assertEquals("添加产线成功", result.getMessage());
        assertNotNull(testLine.getCreateTime(), "应自动设置创建时间");
        assertNotNull(testLine.getUpdateTime(), "应自动设置更新时间");
    }

    @Test
    @DisplayName("添加产线失败 - 产线已存在")
    void testAddLineAlreadyExists() {
        when(lineMapper.findLineByName("SMT-产线A")).thenReturn(testLine);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> lineService.addLine(testLine));
        assertEquals("该产线已存在", exception.getMessage());

        verify(lineMapper, never()).addLine(any());
    }

    @Test
    @DisplayName("添加产线失败 - 数据库插入返回0")
    void testAddLineInsertFailure() {
        when(lineMapper.findLineByName("SMT-产线A")).thenReturn(null);
        when(lineMapper.addLine(any(Line.class))).thenReturn(0);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> lineService.addLine(testLine));
        assertEquals("添加产线失败", exception.getMessage());
    }

    // ==================== 删除产线测试 ====================

    @Test
    @DisplayName("删除产线成功")
    void testDeleteLineSuccess() {
        when(lineMapper.isLineUsed(1L)).thenReturn(0);
        doNothing().when(lineMapper).deleteLine(1L);

        Result result = lineService.deleteLine(1L);

        assertEquals(200, result.getCode());
        assertEquals("删除产线成功", result.getMessage());
        verify(lineMapper).deleteLine(1L);
    }

    @Test
    @DisplayName("删除产线失败 - 产线正在使用中")
    void testDeleteLineInUse() {
        when(lineMapper.isLineUsed(1L)).thenReturn(1);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> lineService.deleteLine(1L));
        assertEquals("该产线正在使用中，请先删除该产线下的工单", exception.getMessage());

        verify(lineMapper, never()).deleteLine(anyLong());
    }

    // ==================== 更新产线测试 ====================

    @Test
    @DisplayName("更新产线成功")
    void testUpdateLineSuccess() {
        when(lineMapper.findLineByName("SMT-产线A")).thenReturn(null);
        doNothing().when(lineMapper).updateLine(any(Line.class));

        Result result = lineService.updateLine(testLine);

        assertEquals(200, result.getCode());
        assertEquals("更新产线成功", result.getMessage());
        assertNotNull(testLine.getUpdateTime(), "应自动设置更新时间");
    }

    @Test
    @DisplayName("更新产线失败 - 产线名称已存在")
    void testUpdateLineNameExists() {
        when(lineMapper.findLineByName("SMT-产线A")).thenReturn(testLine);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> lineService.updateLine(testLine));
        assertEquals("该产线已存在", exception.getMessage());

        verify(lineMapper, never()).updateLine(any());
    }

    // ==================== 查询产线测试 ====================

    @Test
    @DisplayName("根据ID查询产线成功")
    void testFindLineById() {
        when(lineMapper.findLineById(1L)).thenReturn(testLine);

        Result result = lineService.findLineById(1L);

        assertEquals(200, result.getCode());
        assertEquals(testLine, result.getData());
    }

    @Test
    @DisplayName("根据ID查询不存在的产线 - 返回null数据")
    void testFindLineByIdNotFound() {
        when(lineMapper.findLineById(999L)).thenReturn(null);

        Result result = lineService.findLineById(999L);

        assertEquals(200, result.getCode());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("查询所有产线成功")
    void testFindAll() {
        List<Line> lines = Arrays.asList(testLine, new Line());
        when(lineMapper.findAll()).thenReturn(lines);

        Result result = lineService.findAll();

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertInstanceOf(List.class, result.getData());
        assertEquals(2, ((List<?>) result.getData()).size());
    }

    // ==================== 统计查询测试 ====================

    @Test
    @DisplayName("统计指定日期范围内产线产量")
    void testStatistics() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 6, 11);
        List<LineOutputDTO> mockData = List.of();

        when(lineMapper.statistics(start, end)).thenReturn(mockData);

        Result result = lineService.statistics(start, end);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(lineMapper).statistics(start, end);
    }
}
