package cn.eyeo.mall.client.order.dto.data;

import cn.eyeo.mall.client.common.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 订单详情-SKU维度
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/4/5
 */
@Getter
@Setter
public class OrderItemVO extends BaseVO {

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
