package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.QueryProductDTO;
import com.aircookies.smtworkordermanagement.entity.Product;
import com.aircookies.smtworkordermanagement.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 产品管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 添加产品
     */
    @PostMapping("/add")
    public Result addProduct(@RequestBody Product product) {
        log.debug("添加产品: {}", product);
        return productService.addProduct(product);
    }

    /**
     * 根据ID删除产品
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteProduct(@PathVariable Long id) {
        log.debug("删除产品: {}", id);
        return productService.deleteProduct(id);
    }

    /**
     * 批量删除产品
     */
    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        log.debug("批量删除产品: {}", ids);
        return productService.deleteBatch(ids);
    }

    /**
     * 更新产品
     */
    @PutMapping("/update")
    public Result updateProduct(@RequestBody Product product) {
        log.debug("更新产品: {}", product);
        return productService.updateProduct(product);
    }

    /**
     * 根据ID查询产品
     */
    @GetMapping("/find/{id}")
    public Result findProductById(@PathVariable Long id) {
        log.debug("查询产品: {}", id);
        return productService.findProductById(id);
    }

    /**
     * 条件查询产品（分页）
     */
    @PostMapping("/query")
    public Result productList(@RequestBody QueryProductDTO queryProductDTO) {
        log.debug("条件查询产品: {}", queryProductDTO);
        return productService.productList(queryProductDTO);
    }

    /**
     * 查询所有产品（分页）
     */
    @GetMapping("/findAll")
    public Result findAll(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize) {
        log.debug("查询所有产品, 页码: {}, 页大小: {}", pageNum, pageSize);
        return productService.findAll(pageNum, pageSize);
    }
}
