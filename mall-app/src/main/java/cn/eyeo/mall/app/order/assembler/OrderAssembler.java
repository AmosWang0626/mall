package com.amos.mall.order.application.assembler;

import cn.eyeo.mall.client.order.dto.data.CreateOrderCmd;
import cn.eyeo.mall.domain.order.model.OrderEntity;
import cn.eyeo.mall.domain.order.model.OrderItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单工厂
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/12/2
 */
public class OrderAssembler {

    public static OrderEntity build(CreateOrderCmd cmd) {
        List<OrderItem> orderItemList = new ArrayList<>();
        for (CreateOrderCmd.OrderItemParam orderItemParam : cmd.getOrderItemParamList()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setGoodsSkuId(orderItemParam.getGoodsSkuId());
            orderItem.setQuantity(orderItemParam.getQuantity());
            orderItemList.add(orderItem);
        }

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setMemberId(cmd.getMemberId());
        orderEntity.setRecipientName(cmd.getRecipientName());
        orderEntity.setRecipientPhone(cmd.getRecipientPhone());
        orderEntity.setRecipientAddress(cmd.getRecipientAddress());
        orderEntity.setOrderItems(orderItemList);
        return orderEntity;
    }

}
