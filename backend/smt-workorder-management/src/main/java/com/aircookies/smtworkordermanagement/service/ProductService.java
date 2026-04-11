package com.aircookies.smtworkordermanagement.service;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.QueryProductDTO;
import com.aircookies.smtworkordermanagement.entity.Product;

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

    Result deleteBatch(List<Long> ids);
}
