package com.aircookies.smtworkordermanagement.service.impl;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.PagesDTO;
import com.aircookies.smtworkordermanagement.dto.QueryProductDTO;
import com.aircookies.smtworkordermanagement.entity.Product;
import com.aircookies.smtworkordermanagement.mapper.ProductMapper;
import com.aircookies.smtworkordermanagement.service.ProductService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.aircookies.smtworkordermanagement.common.Result.success;

/**
 * 产品服务实现类
 */
@Service
public class ProductServiceImpl implements ProductService {
    
    private final ProductMapper productMapper;
    
    @Autowired
    public ProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }
    
    /**
     * 添加产品
     */
    @Override
    public Result addProduct(Product product) {
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        int res = productMapper.addProduct(product);
        if (res != 0) {
            return success("添加产品成功");
        } else {
            return Result.error("添加产品失败");
        }
    }
    
    /**
     * 根据ID删除产品
     */
    @Override
    public Result deleteProduct(Long id) {
        productMapper.deleteProduct(id);
        return Result.success("删除产品成功");
    }

    /**
     * 批量删除产品
     */
    @Override
    public Result deleteBatch(List<Long> ids) {
        productMapper.deleteBatch(ids);
        return Result.success("批量删除产品成功");
    }
    
    /**
     * 更新产品
     */
    @Override
    public Result updateProduct(Product product) {
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateProduct(product);
        return Result.success("更新产品成功");
    }
    
    /**
     * 根据ID查询产品
     */
    @Override
    public Result findProductById(Long id) {
        Product product = productMapper.findProductById(id);
        return success(product);
    }
    
    /**
     * 条件查询产品（分页）
     */
    @Override
    public Result productList(QueryProductDTO queryProductDTO) {
        // 开启分页
        PageHelper.startPage(queryProductDTO.getPageNum(), queryProductDTO.getPageSize());
        // 查询产品列表
        List<Product> products = productMapper.productList(queryProductDTO);
        // 获取分页结果
        PageInfo<Product> pageInfo = new PageInfo<>(products);
        return success(
                new PagesDTO<>(
                        pageInfo.getPageNum(),
                        pageInfo.getPageSize(),
                        pageInfo.getTotal(),
                        pageInfo.getList()
                )
        );
    }
    
    /**
     * 查询所有产品（分页）
     */
    @Override
    public Result findAll(Integer pageNum, Integer pageSize) {
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);
        // 查询所有产品
        List<Product> products = productMapper.findAll();
        // 获取分页结果
        PageInfo<Product> pageInfo = new PageInfo<>(products);
        // 封装结果并返回
        return success(
                new PagesDTO<>(
                        pageInfo.getPageNum(),
                        pageInfo.getPageSize(),
                        pageInfo.getTotal(),
                        pageInfo.getList()
                )
        );
    }
}
