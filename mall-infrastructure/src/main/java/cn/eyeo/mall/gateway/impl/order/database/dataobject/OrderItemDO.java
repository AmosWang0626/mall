package cn.eyeo.mall.gateway.impl.order.database.dataobject;

import cn.eyeo.mall.common.api.BaseDO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 订单商品条目
 *
 * @author amos.wang
 * @date 2023-03-25 20:17
 */
@Getter
@Setter
public class OrderItemDO extends BaseDO {
    /**
     * 订单ID
     */
    private Long orderInfoId;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 商品sku ID
     */
    private Long goodsSkuId;

    /**
     * 商品sku编号
     */
    private String goodsSkuCode;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 销售属性，机身颜色:白色,内存容量:256G
     */
    private String saleProperties;

    /**
     * 商品毛重
     */
    private BigDecimal goodsGrossWeight;

    /**
     * 购买数量
     */
    private Long purchaseQuantity;

    /**
     * 商品购买价格
     */
    private BigDecimal purchasePrice;

    /**
     * 促销活动ID
     */
    private Long promotionActivityId;

    /**
     * 商品长度
     */
    private BigDecimal goodsLength;

    /**
     * 商品宽度
     */
    private BigDecimal goodsWidth;

    /**
     * 商品高度
     */
    private BigDecimal goodsHeight;
}