package com.mall.module.prize.entity;

import lombok.Data;

import java.util.Date;

/**
 * 奖池领取记录
 */
@Data
public class PrizeClaimLog {
    private Long id;
    private Long poolId;
    private Long userId;
    private Long couponId;
    private Date claimTime;
    private Date createTime;
}
