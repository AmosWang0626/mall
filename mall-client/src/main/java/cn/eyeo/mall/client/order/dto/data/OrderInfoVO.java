package cn.eyeo.mall.client.order.dto.data;

import cn.eyeo.mall.client.common.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单信息
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/4/5
 */
@Getter
@Setter
public class OrderInfoVO extends BaseVO {

    /**
     * 订单编号
     */
    private Long orderId;

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
    private List<OrderItemVO> orderItems;

    /**
     * 商品总金额
     */
    private BigDecimal totalPrice;

    /**
     * 运费
     */
    private BigDecimal shippingFee;

    /**
     * 订单金额，等于商品总金额加运费
     */
    private BigDecimal orderPrice;

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

}
