package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.QueryProductDTO;
import com.aircookies.smtworkordermanagement.entity.Product;
import com.aircookies.smtworkordermanagement.service.ProductService;
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
@DisplayName("产品控制器测试")
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Product testProduct;

    @BeforeEach
    @DisplayName("初始化测试环境")
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setCode("P001");
        testProduct.setName("测试产品");
        testProduct.setSpec("规格A");
    }

    @Test
    @DisplayName("添加产品")
    void testAddProduct() throws Exception {
        Result expectedResult = Result.success("添加产品成功");
        when(productService.addProduct(any(Product.class))).thenReturn(expectedResult);

        mockMvc.perform(post("/product/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(productService).addProduct(any(Product.class));
    }

    @Test
    @DisplayName("根据ID删除产品")
    void testDeleteProduct() throws Exception {
        Result expectedResult = Result.success("删除产品成功");
        when(productService.deleteProduct(1L)).thenReturn(expectedResult);

        mockMvc.perform(delete("/product/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(productService).deleteProduct(1L);
    }

    @Test
    @DisplayName("批量删除产品")
    void testDeleteBatch() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        Result expectedResult = Result.success("批量删除成功");
        when(productService.deleteBatch(ids)).thenReturn(expectedResult);

        mockMvc.perform(delete("/product/deleteBatch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(productService).deleteBatch(ids);
    }

    @Test
    @DisplayName("更新产品")
    void testUpdateProduct() throws Exception {
        Result expectedResult = Result.success("更新产品成功");
        when(productService.updateProduct(any(Product.class))).thenReturn(expectedResult);

        mockMvc.perform(put("/product/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(productService).updateProduct(any(Product.class));
    }

    @Test
    @DisplayName("根据ID查询产品")
    void testFindProductById() throws Exception {
        Result expectedResult = Result.success(testProduct);
        when(productService.findProductById(1L)).thenReturn(expectedResult);

        mockMvc.perform(get("/product/find/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(productService).findProductById(1L);
    }

    @Test
    @DisplayName("条件查询产品 - POST方式")
    void testProductList() throws Exception {
        QueryProductDTO queryDTO = new QueryProductDTO();
        queryDTO.setName("测试产品");
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Result expectedResult = Result.success(Collections.singletonList(testProduct));
        when(productService.queryProduct(any(QueryProductDTO.class))).thenReturn(expectedResult);

        mockMvc.perform(post("/product/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(productService).queryProduct(any(QueryProductDTO.class));
    }

    @Test
    @DisplayName("分页查询所有产品")
    void testFindAll() throws Exception {
        Result expectedResult = Result.success(Collections.singletonList(testProduct));
        when(productService.findAll(1, 10)).thenReturn(expectedResult);

        mockMvc.perform(get("/product/findAll")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(productService).findAll(1, 10);
    }

    @Test
    @DisplayName("产品产量统计 - 指定日期范围")
    void testStatistics() throws Exception {
        LocalDate startTime = LocalDate.of(2026, 6, 1);
        LocalDate endTime = LocalDate.of(2026, 6, 10);
        Result expectedResult = Result.success(List.of());

        when(productService.statistics(startTime, endTime)).thenReturn(expectedResult);

        mockMvc.perform(get("/product/statistics")
                        .param("startTime", "2026-06-01")
                        .param("endTime", "2026-06-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(productService).statistics(startTime, endTime);
    }
}