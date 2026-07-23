package com.mall.module.product.entity;

import com.mall.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

/**
 * 商品SKU
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductSku extends BaseEntity {
    private Long id;
    private Long productId;
    private String skuCode;
    private String name;
    private String specs;
    private BigDecimal price;
    private Integer stock;
    private String image;
    private Integer status;
}
