package com.aircookies.smtworkordermanagement.service;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.QueryProductDTO;
import com.aircookies.smtworkordermanagement.entity.Product;

import java.time.LocalDate;
import java.util.List;

/**
 * 产品服务接口
 */
public interface ProductService {
    
    /**
     * 添加产品
     */
    Result addProduct(Product product);
    
    /**
     * 根据ID删除产品
     */
    Result deleteProduct(Long id);
    
    /**
     * 更新产品
     */
    Result updateProduct(Product product);
    
    /**
     * 根据ID查询产品
     */
    Result findProductById(Long id);
    
    /**
     * 条件查询产品（分页）
     */
    Result productList(QueryProductDTO queryProductDTO);
    
    /**
     * 查询所有产品（分页）
     */
    Result findAll(Integer pageNum, Integer pageSize);

    /**
    * 批量删除产品
    */
    Result deleteBatch(List<Long> ids);

    /**
     * 查询指定日期内各个产品的产量统计
     */
    Result statistics(LocalDate startTime, LocalDate endTime);
}
