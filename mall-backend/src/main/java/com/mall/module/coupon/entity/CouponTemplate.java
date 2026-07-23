package com.mall.module.coupon.entity;

import com.mall.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class CouponTemplate extends BaseEntity {
    private Long id;
    private String name;
    private Integer type; // 1-满减, 2-折扣, 3-无门槛
    private BigDecimal faceValue;
    private BigDecimal discount;
    private BigDecimal minSpend;
    private Long categoryLimit;
    private Integer totalCount;
    private Integer issuedCount;
    private Integer perLimit;
    private Integer validType; // 1-固定日期, 2-领取后N天
    private Date validStart;
    private Date validEnd;
    private Integer validDays;
    private Integer status;
}
