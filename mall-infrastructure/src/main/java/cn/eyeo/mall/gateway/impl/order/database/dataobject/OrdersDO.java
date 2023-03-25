package cn.eyeo.mall.gateway.impl.order.database.dataobject;

import cn.eyeo.mall.common.api.BaseDO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单表
 *
 * @author amos.wang
 * @date 2023-03-25 20:17
 */
@Getter
@Setter
public class OrdersDO extends BaseDO {
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
     * 支付方式，如支付宝、微信支付等
     */
    private String paymentMethod;

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
     * 下单时间
     */
    private LocalDateTime orderTime;

    /**
     * 订单状态，如待付款、待发货、已发货等
     */
    private String orderStatus;
}