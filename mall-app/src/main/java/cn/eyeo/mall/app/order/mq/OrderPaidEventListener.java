package cn.eyeo.mall.app.order.mq;

import cn.eyeo.mall.client.order.api.IOrderService;
import cn.eyeo.mall.client.order.dto.data.OrderPaidCmd;
import cn.eyeo.mall.mq.RocketMQConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 订单支付事件监听
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/12/3
 */
@Component
public class OrderPaidEventListener {

    @Autowired
    private IOrderService iOrderService;

    public void start() {
        // 监听指定 topic，消费消息
        RocketMQConsumer consumer = new RocketMQConsumer();
        consumer.subscribe("order_paid_event_topic");
        consumer.consume(message -> {
            try {
                OrderPaidCmd cmd = new OrderPaidCmd();
                cmd.setOrderId(Long.valueOf(message));
                iOrderService.paid(cmd);
            } catch (Exception e) {
                e.printStackTrace();
                // 重试多次，多次还失败的话，人工介入处理和处理死信队列
            }
        });
    }

}
