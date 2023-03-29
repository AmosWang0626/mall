package cn.eyeo.mall.gateway.impl.order.database.mapper;

import cn.eyeo.mall.gateway.impl.order.database.dataobject.OrderInfoDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderInfoDO row);

    OrderInfoDO selectByPrimaryKey(Long id);

    List<OrderInfoDO> selectAll();

    int updateByPrimaryKey(OrderInfoDO row);

    int updateStatus(OrderInfoDO orderDO);

    List<OrderInfoDO> findUnpaidOrder();

}