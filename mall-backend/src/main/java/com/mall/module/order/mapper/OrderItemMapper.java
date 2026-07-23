package com.mall.module.order.mapper;

import com.mall.module.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OrderItemMapper {
    int batchInsert(@Param("list") List<OrderItem> list);
    List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);
    List<OrderItem> selectByOrderIds(@Param("orderIds") List<Long> orderIds);
}
