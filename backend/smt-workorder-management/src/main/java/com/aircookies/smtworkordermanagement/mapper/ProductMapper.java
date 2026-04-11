package com.aircookies.smtworkordermanagement.mapper;

import com.aircookies.smtworkordermanagement.dto.QueryProductDTO;
import com.aircookies.smtworkordermanagement.entity.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 产品数据访问层
 */
@Mapper
public interface ProductMapper {
    
    /**
     * 添加产品
     */
    int addProduct(Product product);
    
    /**
     * 根据ID删除产品
     */
    void deleteProduct(Long id);
    
    /**
     * 更新产品
     */
    void updateProduct(Product product);
    
    /**
     * 根据ID查询产品
     */
    Product findProductById(Long id);
    
    /**
     * 查询所有产品（支持条件查询）
     */
    List<Product> productList(QueryProductDTO queryProductDTO);
    
    /**
     * 查询所有产品
     */
    List<Product> findAll();

    /**
     * 批量删除产品
     */
    void deleteBatch(List<Long> ids);
}
