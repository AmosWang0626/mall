package com.mall.module.marketing.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class ActivityProduct {
    private Long id;
    private Long activityId;
    private Long productId;
    private Long skuId;
    private BigDecimal activityPrice;
    private Integer activityStock;
    private Integer limitPerUser;
    private Integer sort;
    private Date createTime;
    // transient
    private String productName;
    private String productImage;
}
