package cn.eyeo.mall.gateway.impl.order.repository;

import cn.eyeo.mall.client.common.SystemErrorCode;
import cn.eyeo.mall.common.exception.MallBizException;
import cn.eyeo.mall.domain.order.model.OrderEntity;
import cn.eyeo.mall.domain.order.model.OrderId;
import cn.eyeo.mall.gateway.impl.order.convertor.OrderConvertor;
import cn.eyeo.mall.gateway.impl.order.database.dataobject.OrderInfoDO;
import cn.eyeo.mall.gateway.impl.order.database.dataobject.OrderItemDO;
import cn.eyeo.mall.gateway.impl.order.database.mapper.OrderInfoMapper;
import cn.eyeo.mall.gateway.impl.order.database.mapper.OrderItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 订单资源库
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/12/2
 */
@Repository
public class OrderRepository {

    @Autowired
    private OrderInfoMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    public void save(OrderEntity orderEntity) {
        OrderInfoDO orderDO = OrderConvertor.toDO(orderEntity);
        int modifyLines = orderMapper.insert(orderDO);
        checkModifyLine(modifyLines, "保存订单失败!");

        // 订单ID
        Long orderId = orderDO.getId();
        orderEntity.setOrderId(new OrderId().setId(orderId));

        List<OrderItemDO> orderItemDOList = OrderConvertor.toItemDO(orderId, orderEntity.getOrderItems());
        orderItemDOList.forEach(item -> {
            int lines = orderItemMapper.insert(item);
            checkModifyLine(lines, "保存订单条目失败!");
        });
    }

    public void updateStatus(OrderEntity orderEntity) {
        Long orderId = orderEntity.getOrderId().getId();
        String orderStatus = orderEntity.getStatus();

        int modifyLines = orderMapper.updateStatus(orderId, orderStatus, LocalDateTime.now());
        checkModifyLine(modifyLines, "修改订单状态失败!");
    }

    public OrderEntity findById(OrderId orderId) {
        OrderInfoDO orderDO = orderMapper.selectByPrimaryKey(orderId.getId());
        if (Objects.isNull(orderDO)) {
            return null;
        }

        List<OrderItemDO> orderItemList = orderItemMapper.findByOrderId(orderId.getId());
        return OrderConvertor.toEntity(orderDO, orderItemList);
    }

    public List<OrderEntity> findUnpaidOrder(LocalDateTime lastTime) {
        List<OrderInfoDO> unpaidOrder = orderMapper.findUnpaidOrder(lastTime);
        return unpaidOrder.stream().map(OrderConvertor::toEntity).collect(Collectors.toList());
    }

    public void checkModifyLine(int lines, String message) {
        if (lines < 1) {
            throw new MallBizException(SystemErrorCode.S_DB_WRITE_ERROR);
        }
    }

}
