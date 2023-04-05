package cn.eyeo.mall.gateway.impl.order.database.mapper;

import cn.eyeo.mall.gateway.impl.order.database.dataobject.OrderInfoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderInfoDO row);

    OrderInfoDO selectByPrimaryKey(Long id);

    List<OrderInfoDO> selectAll();

    int updateByPrimaryKey(OrderInfoDO row);

    int updateStatus(@Param("id") Long id,
                     @Param("orderStatus") String orderStatus,
                     @Param("gmtModified") LocalDateTime gmtModified);

    List<OrderInfoDO> findUnpaidOrder(LocalDateTime lastTime);

}