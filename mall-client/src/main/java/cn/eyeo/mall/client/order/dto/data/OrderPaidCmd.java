package cn.eyeo.mall.client.order.dto.data;

import com.alibaba.cola.dto.Command;
import lombok.Getter;
import lombok.Setter;

/**
 * 订单支付数据
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/3/29
 */
@Getter
@Setter
public class OrderPaidCmd extends Command {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 支付结果
     */
    private String paymentResult;

}
