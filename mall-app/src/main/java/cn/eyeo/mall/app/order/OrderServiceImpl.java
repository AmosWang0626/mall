package cn.eyeo.mall.app.order;

import cn.eyeo.mall.app.order.assembler.OrderAssembler;
import cn.eyeo.mall.app.order.domain.OrderDomainService;
import cn.eyeo.mall.client.order.api.IOrderService;
import cn.eyeo.mall.client.order.dto.data.CreateOrderCmd;
import cn.eyeo.mall.client.order.dto.data.OrderInfoVO;
import cn.eyeo.mall.client.order.dto.data.OrderPaidCmd;
import cn.eyeo.mall.client.order.dto.data.OrderStatus;
import cn.eyeo.mall.domain.order.model.OrderEntity;
import cn.eyeo.mall.domain.order.model.OrderId;
import cn.eyeo.mall.gateway.impl.order.repository.OrderRepository;
import com.alibaba.cola.dto.SingleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 订单应用服务
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/12/1
 */
@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private OrderDomainService orderDomainService;
    @Autowired
    private OrderRepository orderRepository;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public SingleResponse<OrderInfoVO> create(CreateOrderCmd cmd) {
        OrderEntity orderEntity = OrderAssembler.build(cmd);

        // 校验库存
        orderDomainService.checkStockEnough(orderEntity);

        // 计算价格
        orderDomainService.calculatePrice(orderEntity);

        // 保存订单
        orderRepository.save(orderEntity);

        // 锁定库存
        orderDomainService.lockStock(orderEntity);

        // 修改订单状态
        orderEntity.updateStatus(OrderStatus.PENDING_PAYMENT);
        orderRepository.updateStatus(orderEntity);

        return SingleResponse.of(OrderAssembler.toVO(orderEntity));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void paid(OrderPaidCmd cmd) {
        // 根据ID查询订单
        OrderId orderId = new OrderId().setId(cmd.getOrderId());
        OrderEntity orderEntity = orderRepository.findById(orderId);
        if (Objects.isNull(orderEntity)) {
            return;
        }

        // 更新订单状态
        orderEntity.setStatus(OrderStatus.PROCESSING.name());
        orderRepository.updateStatus(orderEntity);

        // 扣减库存
        orderDomainService.reduceStock(orderEntity);
    }

    @Override
    public void autoCancel(LocalDateTime lastTime) {
        // 长时间未支付的订单自动取消
        List<OrderEntity> unpaidOrder = orderRepository.findUnpaidOrder(lastTime);
        unpaidOrder.forEach(orderEntity -> {
            orderEntity.setStatus(OrderStatus.CANCELED.name());
            orderRepository.updateStatus(orderEntity);
        });
    }
}
