package com.mall.module.product.mapper;

import com.mall.module.product.entity.ProductSku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductSkuMapper {
    int insert(ProductSku sku);
    int updateById(ProductSku sku);
    int deleteById(Long id);
    ProductSku selectById(Long id);
    List<ProductSku> selectByProductId(@Param("productId") Long productId);
    int deleteByProductId(@Param("productId") Long productId);
    int batchInsert(@Param("list") List<ProductSku> list);
    int reduceStock(@Param("id") Long id, @Param("quantity") int quantity);
    int restoreStock(@Param("id") Long id, @Param("quantity") int quantity);
    int sumStockByProductId(@Param("productId") Long productId);
}
