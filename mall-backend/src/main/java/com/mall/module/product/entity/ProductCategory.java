package com.mall.module.product.entity;

import com.mall.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

/**
 * 商品分类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductCategory extends BaseEntity {
    private Long id;
    private Long parentId;
    private String name;
    private String icon;
    private Integer sort;
    private Integer status;
    private Integer level;

    /** 子分类列表(非DB字段) */
    private transient List<ProductCategory> children;
}
