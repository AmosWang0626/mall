package com.mall.module.product.mapper;

import com.mall.module.product.entity.ProductCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductCategoryMapper {
    int insert(ProductCategory category);
    int updateById(ProductCategory category);
    int deleteById(Long id);
    ProductCategory selectById(Long id);

    List<ProductCategory> selectAll(@Param("status") Integer status);

    List<ProductCategory> selectByParentId(@Param("parentId") Long parentId, @Param("status") Integer status);

    List<ProductCategory> selectByLevel(@Param("level") int level);

    int selectMaxSort(@Param("parentId") Long parentId);

    long countByName(@Param("name") String name, @Param("parentId") Long parentId, @Param("excludeId") Long excludeId);
}
