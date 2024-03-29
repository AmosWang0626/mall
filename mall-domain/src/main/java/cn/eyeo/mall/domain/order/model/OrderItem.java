package cn.eyeo.mall.domain.order.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 订单条目
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/12/1
 */
@Getter
@Setter
public class OrderItem {

    /**
     * 商品SKU ID
     */
    private Long goodsSkuId;
    /**
     * 购买数量
     */
    private Integer quantity;
    /**
     * 商品价格
     */
    private BigDecimal price;

}
