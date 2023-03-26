package cn.eyeo.mall.gateway.impl.product.database.mapper;

import cn.eyeo.mall.gateway.impl.product.database.dataobject.ProductCategoryDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductCategoryMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProductCategoryDO row);

    ProductCategoryDO selectByPrimaryKey(Long id);

    List<ProductCategoryDO> selectAll();

    int updateByPrimaryKey(ProductCategoryDO row);

    int updateIsLeafById(@Param("isLeaf") Boolean isLeaf, @Param("id") Long id);

    Integer countByParentId(Long parentId);
}