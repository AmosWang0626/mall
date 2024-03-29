package cn.eyeo.mall.domain.order.model;

import cn.eyeo.mall.client.order.dto.data.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单实体(聚合根)
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/11/27
 */
@Getter
@Setter
public class OrderEntity {

    /**
     * 订单编号
     */
    private OrderId orderId;

    /**
     * 用户ID
     */
    private Long memberId;
    /**
     * 收货人姓名
     */
    private String recipientName;

    /**
     * 收货人联系电话
     */
    private String recipientPhone;

    /**
     * 收货地址
     */
    private String recipientAddress;

    /**
     * 订单条目
     */
    private List<OrderItem> orderItems;

    /**
     * 商品总金额
     */
    private BigDecimal totalPrice;

    /**
     * 运费
     */
    private BigDecimal shippingFee;

    /**
     * 订单状态
     *
     * @see OrderStatus
     */
    private String status;

    /**
     * 更新订单状态
     *
     * @param orderStatus 订单状态
     */
    public void updateStatus(OrderStatus orderStatus) {
        this.status = orderStatus.name();
    }

    /**
     * 订单金额，等于商品总金额加运费
     */
    public BigDecimal getOrderPrice() {
        return totalPrice.add(shippingFee);
    }
}
