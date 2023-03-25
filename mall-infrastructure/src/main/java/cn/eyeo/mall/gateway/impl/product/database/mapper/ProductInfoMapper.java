package cn.eyeo.mall.gateway.impl.product.database.mapper;

import cn.eyeo.mall.gateway.impl.product.database.dataobject.ProductDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProductDO row);

    ProductDO selectByPrimaryKey(Long id);

    List<ProductDO> selectAll();

    int updateByPrimaryKey(ProductDO row);
}