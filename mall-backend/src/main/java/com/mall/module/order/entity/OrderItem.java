package com.mall.module.order.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderItem {
    private Long id;
    private Long orderId;
    private String orderNo;
    private Long productId;
    private Long skuId;
    private String productName;
    private String productImage;
    private String skuName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalAmount;
    private Date createTime;
}
