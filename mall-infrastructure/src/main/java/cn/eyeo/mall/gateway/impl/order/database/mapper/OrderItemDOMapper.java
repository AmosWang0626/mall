package cn.eyeo.mall.gateway.impl.order.database.mapper;

import cn.eyeo.mall.gateway.impl.order.database.dataobject.OrderItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderItemDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderItemDO row);

    OrderItemDO selectByPrimaryKey(Long id);

    List<OrderItemDO> selectAll();

    int updateByPrimaryKey(OrderItemDO row);
}