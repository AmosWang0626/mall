package cn.eyeo.mall.gateway.impl.order.database.mapper;

import cn.eyeo.mall.gateway.impl.order.database.dataobject.OrdersDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrdersDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrdersDO row);

    OrdersDO selectByPrimaryKey(Long id);

    List<OrdersDO> selectAll();

    int updateByPrimaryKey(OrdersDO row);
}