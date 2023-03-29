package cn.eyeo.mall.gateway.impl.order.convertor;

import cn.eyeo.mall.gateway.impl.order.database.dataobject.OrderInfoDO;
import cn.eyeo.mall.gateway.impl.order.database.dataobject.OrderItemDO;
import cn.eyeo.mall.domain.order.model.OrderEntity;
import cn.eyeo.mall.domain.order.model.OrderId;
import cn.eyeo.mall.domain.order.model.OrderItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 订单对象转换
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/12/2
 */
public class OrderConvertor {

    public static OrderInfoDO toDO(OrderEntity entity) {
        OrderInfoDO orderDO = new OrderInfoDO();
        if (Objects.isNull(entity.getOrderId())) {
            orderDO.setGmtCreate(LocalDateTime.now());
        }
        orderDO.setOrderStatus(entity.getStatus());
        orderDO.setGmtModified(LocalDateTime.now());
        return orderDO;
    }

    public static List<OrderItemDO> toItemDO(Long orderId, List<OrderItem> orderItemList) {
        return orderItemList.stream()
                .map(item -> toDO(orderId, item))
                .collect(Collectors.toList());
    }

    public static OrderItemDO toDO(Long orderId, OrderItem orderItem) {
        OrderItemDO orderItemDO = new OrderItemDO();
        orderItemDO.setOrderInfoId(orderId);
        orderItemDO.setGoodsSkuId(orderItem.getGoodsSkuId());
        orderItemDO.setPurchaseQuantity(orderItem.getPurchaseQuantity());
        return orderItemDO;
    }

    public static OrderEntity toEntity(OrderInfoDO orderDO) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(new OrderId().setId(orderDO.getId()));
        orderEntity.setStatus(orderDO.getOrderStatus());
        return orderEntity;
    }

    public static OrderEntity toEntity(OrderInfoDO orderDO, List<OrderItemDO> orderItemList) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(new OrderId().setId(orderDO.getId()));
        orderEntity.setStatus(orderDO.getOrderStatus());

        List<OrderItem> orderItems = orderItemList.stream().map(OrderConvertor::toEntity).collect(Collectors.toList());
        orderEntity.setOrderItems(orderItems);

        return orderEntity;
    }

    public static OrderItem toEntity(OrderItemDO orderItemDO) {
        OrderItem orderItem = new OrderItem();
        orderItem.setGoodsSkuId(orderItemDO.getGoodsSkuId());
        orderItem.setPurchaseQuantity(orderItemDO.getPurchaseQuantity());
        return orderItem;
    }

}
