package cn.eyeo.mall.gateway.impl.product.database.mapper;

import cn.eyeo.mall.client.product.dto.query.ProductPageQuery;
import cn.eyeo.mall.gateway.impl.product.database.dataobject.ProductDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {

    int deleteByPrimaryKey(Long id);

    int insert(ProductDO row);

    ProductDO selectByPrimaryKey(Long id);

    List<ProductDO> selectAll();

    int updateByPrimaryKey(ProductDO row);

    int updateStatusById(ProductDO productDO);

    List<ProductDO> selectByParam(ProductPageQuery cmd);

    Integer countByParam(ProductPageQuery cmd);

    Integer countByCategoryId(Long categoryId);

}