package com.mall.module.coupon.entity;

import lombok.Data;
import java.util.Date;

@Data
public class UserCoupon {
    private Long id;
    private Long userId;
    private Long couponId;
    private Integer status; // 0-未使用, 1-已使用, 2-已过期
    private Long orderId;
    private Date validStart;
    private Date validEnd;
    private Date receiveTime;
    private Date useTime;
    private Date createTime;
    private Date updateTime;
    // transient
    private CouponTemplate coupon;
}
