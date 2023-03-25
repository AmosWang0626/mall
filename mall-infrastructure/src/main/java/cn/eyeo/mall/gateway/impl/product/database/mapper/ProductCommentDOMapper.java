package cn.eyeo.mall.gateway.impl.product.database.mapper;

import cn.eyeo.mall.gateway.impl.product.database.dataobject.ProductCommentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductCommentDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProductCommentDO row);

    ProductCommentDO selectByPrimaryKey(Long id);

    List<ProductCommentDO> selectAll();

    int updateByPrimaryKey(ProductCommentDO row);
}