package com.mall.module.product.entity;

import com.mall.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品SPU
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseEntity {
    private Long id;
    private Long categoryId;
    private String name;
    private String subtitle;
    private String mainImage;
    private String subImages;
    private String detail;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private BigDecimal cost;
    private Integer stock;
    private Integer sales;
    private Integer status;  // 0-下架, 1-上架, 2-草稿
    private Integer sort;
    private String tags;

    /** 分类名称(非DB字段, 查询时JOIN获取) */
    private transient String categoryName;

    /** SKU列表(非DB字段) */
    private transient List<ProductSku> skuList;
}
