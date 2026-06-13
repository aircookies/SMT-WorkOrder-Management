package com.aircookies.smtworkordermanagement.service;

import com.aircookies.smtworkordermanagement.common.BusinessException;
import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.PagesDTO;
import com.aircookies.smtworkordermanagement.dto.ProductionQuantityDTO;
import com.aircookies.smtworkordermanagement.dto.QueryProductDTO;
import com.aircookies.smtworkordermanagement.entity.Product;
import com.aircookies.smtworkordermanagement.mapper.ProductMapper;
import com.aircookies.smtworkordermanagement.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("产品服务测试")
public class ProductServiceImplTest {

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;

    @BeforeEach
    @DisplayName("初始化测试数据")
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setCode("P001");
        testProduct.setName("测试产品");
        testProduct.setSpec("100x200mm");
        testProduct.setImage("product.jpg");
    }

    // ==================== 添加产品测试 ====================

    @Test
    @DisplayName("添加产品成功")
    void testAddProductSuccess() {
        when(productMapper.addProduct(any(Product.class))).thenReturn(1);

        Result result = productService.addProduct(testProduct);

        assertEquals(200, result.getCode());
        assertEquals("添加产品成功", result.getMessage());
        assertNotNull(testProduct.getCreateTime(), "应自动设置创建时间");
        assertNotNull(testProduct.getUpdateTime(), "应自动设置更新时间");
    }

    @Test
    @DisplayName("添加产品失败 - 数据库插入返回0")
    void testAddProductFailure() {
        when(productMapper.addProduct(any(Product.class))).thenReturn(0);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> productService.addProduct(testProduct));
        assertEquals("添加产品失败", exception.getMessage());
    }

    // ==================== 删除产品测试 ====================

    @Test
    @DisplayName("删除产品成功")
    void testDeleteProductSuccess() {
        when(productMapper.isProductProducing(1L)).thenReturn(0);
        doNothing().when(productMapper).deleteProduct(1L);

        Result result = productService.deleteProduct(1L);

        assertEquals(200, result.getCode());
        assertEquals("删除产品成功", result.getMessage());
        verify(productMapper).deleteProduct(1L);
    }

    @Test
    @DisplayName("删除产品失败 - 产品正在生产中")
    void testDeleteProductInProduction() {
        when(productMapper.isProductProducing(1L)).thenReturn(1);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> productService.deleteProduct(1L));
        assertEquals("该产品正在生产中，请先停止该产品下的所有工单", exception.getMessage());

        verify(productMapper, never()).deleteProduct(anyLong());
    }

    // ==================== 批量删除产品测试 ====================

    @Test
    @DisplayName("批量删除产品成功")
    void testDeleteBatchSuccess() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        when(productMapper.isProductProducing(anyLong())).thenReturn(0);
        doNothing().when(productMapper).deleteBatch(ids);

        Result result = productService.deleteBatch(ids);

        assertEquals(200, result.getCode());
        assertEquals("批量删除产品成功", result.getMessage());
        verify(productMapper).deleteBatch(ids);
    }

    @Test
    @DisplayName("批量删除产品失败 - 某个产品正在生产中")
    void testDeleteBatchWithProducingProduct() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        when(productMapper.isProductProducing(1L)).thenReturn(0);
        when(productMapper.isProductProducing(2L)).thenReturn(1);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> productService.deleteBatch(ids));
        assertEquals("该产品正在生产中，请先停止该产品下的所有工单", exception.getMessage());

        verify(productMapper, never()).deleteBatch(anyList());
    }

    // ==================== 更新产品测试 ====================

    @Test
    @DisplayName("更新产品成功")
    void testUpdateProductSuccess() {
        when(productMapper.findProductById(1L)).thenReturn(testProduct);
        when(productMapper.updateProduct(any(Product.class))).thenReturn(1);

        Result result = productService.updateProduct(testProduct);

        assertEquals(200, result.getCode());
        assertEquals("更新产品成功", result.getMessage());
        assertNotNull(testProduct.getUpdateTime(), "应自动设置更新时间");
    }

    @Test
    @DisplayName("更新产品失败 - 产品不存在")
    void testUpdateProductNotFound() {
        when(productMapper.findProductById(1L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> productService.updateProduct(testProduct));
        assertEquals("该产品不存在", exception.getMessage());

        verify(productMapper, never()).updateProduct(any());
    }

    @Test
    @DisplayName("更新产品失败 - 数据库更新返回0")
    void testUpdateProductFailure() {
        when(productMapper.findProductById(1L)).thenReturn(testProduct);
        when(productMapper.updateProduct(any(Product.class))).thenReturn(0);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> productService.updateProduct(testProduct));
        assertEquals("更新产品失败", exception.getMessage());
    }

    // ==================== 查询产品测试 ====================

    @Test
    @DisplayName("根据ID查询产品成功")
    void testFindProductById() {
        when(productMapper.findProductById(1L)).thenReturn(testProduct);

        Result result = productService.findProductById(1L);

        assertEquals(200, result.getCode());
        assertEquals(testProduct, result.getData());
    }

    @Test
    @DisplayName("根据ID查询不存在的产品 - 返回null数据")
    void testFindProductByIdNotFound() {
        when(productMapper.findProductById(999L)).thenReturn(null);

        Result result = productService.findProductById(999L);

        assertEquals(200, result.getCode());
        assertNull(result.getData());
    }

    // ==================== 统计查询测试 ====================

    @Test
    @DisplayName("查询指定日期内各产品产量统计")
    void testStatistics() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 6, 11);
        List<ProductionQuantityDTO> mockData = List.of();

        when(productMapper.statistics(start, end)).thenReturn(mockData);

        Result result = productService.statistics(start, end);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(productMapper).statistics(start, end);
    }

    // ==================== 分页查询测试 ====================

    @Test
    @DisplayName("分页查询所有产品成功")
    void testFindAllWithPagination() {
        List<Product> products = Collections.singletonList(testProduct);
        when(productMapper.findAll()).thenReturn(products);

        Result result = productService.findAll(1, 10);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertInstanceOf(PagesDTO.class, result.getData());
    }

    @Test
    @DisplayName("查询所有产品不使用分页成功")
    void testFindAllWithoutPagination() {
        List<Product> products = Collections.singletonList(testProduct);
        when(productMapper.findAll()).thenReturn(products);

        Result result = productService.findAll(null, null);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertInstanceOf(List.class, result.getData());
        assertEquals(1, ((List<?>) result.getData()).size());
    }

    @Test
    @DisplayName("条件查询产品列表成功")
    void testProductList() {
        QueryProductDTO queryDTO = new QueryProductDTO();
        queryDTO.setName("测试");
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        List<Product> products = Collections.singletonList(testProduct);
        when(productMapper.productList(queryDTO)).thenReturn(products);

        Result result = productService.productList(queryDTO);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertInstanceOf(PagesDTO.class, result.getData());
    }
}