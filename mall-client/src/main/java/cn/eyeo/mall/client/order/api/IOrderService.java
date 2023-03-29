package cn.eyeo.mall.client.order.api;

import cn.eyeo.mall.client.order.dto.data.CreateOrderCmd;
import cn.eyeo.mall.client.order.dto.data.OrderPaidCmd;

/**
 * 用户相关
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/1/8
 */
public interface IOrderService {

    /**
     * 创建订单业务流程
     *
     * @param cmd 创建订单参数
     * @return true-创建成功
     */
    boolean create(CreateOrderCmd cmd);

    /**
     * 订单支付事件处理
     *
     * @param cmd 订单支付参数
     */
    void paid(OrderPaidCmd cmd);

    /**
     * 自动取消订单
     */
    void autoCancel();

}
