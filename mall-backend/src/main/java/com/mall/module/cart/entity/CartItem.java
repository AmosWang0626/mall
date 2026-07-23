package com.mall.module.cart.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class CartItem {
    private Long id;
    private Long userId;
    private Long productId;
    private Long skuId;
    private String productName;
    private String productImage;
    private String skuName;
    private BigDecimal price;
    private Integer quantity;
    private Integer selected;
    private Date createTime;
    private Date updateTime;
}
