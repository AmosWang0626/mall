package com.mall.module.product.dto;

import com.mall.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品分页查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductQueryDTO extends PageRequest {

    /** 分类ID */
    private Long categoryId;

    /** 商品名称 (模糊查询) */
    private String name;

    /** 状态: 0-下架, 1-上架 */
    private Integer status;

    /** 标签 (模糊查询) */
    private String tags;
}
