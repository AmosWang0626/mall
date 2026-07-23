package com.mall.module.product.mapper;

import com.mall.module.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductMapper {
    int insert(Product product);
    int updateById(Product product);
    int deleteById(Long id);
    Product selectById(Long id);

    /** PageHelper分页查询(不带offset/limit, 由PageHelper拦截器处理) */
    List<Product> selectList(@Param("categoryId") Long categoryId,
                             @Param("name") String name,
                             @Param("status") Integer status,
                             @Param("tags") String tags);

    long count(@Param("categoryId") Long categoryId,
               @Param("name") String name,
               @Param("status") Integer status,
               @Param("tags") String tags);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    int updateStock(@Param("id") Long id, @Param("stock") int stock);
    int reduceStock(@Param("id") Long id, @Param("quantity") int quantity);
    int restoreStock(@Param("id") Long id, @Param("quantity") int quantity);
    int addSales(@Param("id") Long id, @Param("quantity") int quantity);
    int increaseSales(@Param("id") Long id, @Param("quantity") int quantity);
}
