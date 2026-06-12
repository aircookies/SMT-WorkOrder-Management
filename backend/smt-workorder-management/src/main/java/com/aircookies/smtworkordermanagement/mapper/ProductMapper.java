package com.aircookies.smtworkordermanagement.mapper;

import com.aircookies.smtworkordermanagement.dto.ProductionQuantityDTO;
import com.aircookies.smtworkordermanagement.dto.QueryProductDTO;
import com.aircookies.smtworkordermanagement.entity.Product;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

/**
 * 产品数据访问层
 */
@Mapper
public interface ProductMapper {

    int addProduct(Product product);

    void deleteProduct(Long id);

    int updateProduct(Product product);

    Product findProductById(Long id);

    List<Product> productList(QueryProductDTO queryProductDTO);

    List<Product> findAll();

    void deleteBatch(List<Long> ids);

    List<ProductionQuantityDTO> statistics(LocalDate startTime, LocalDate endTime);

    int isProductProducing(Long productId);
}
