package cn.eyeo.mall.gateway.impl.product.database.dataobject;

import cn.eyeo.mall.common.api.BaseDO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 商品表
 *
 * @author amos.wang
 * @date 2023-03-25 20:17
 */
@Getter
@Setter
public class ProductDO extends BaseDO {
    /**
     * 商品名称
     */
    private String name;

    /**
     * 图片
     */
    private String image;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品库存
     */
    private Long stock;

    /**
     * 商品状态：0-下架，1-上架
     */
    private Boolean status;

    /**
     * 商品分类ID
     */
    private Long categoryId;

    /**
     * 商品描述
     */
    private String description;
}