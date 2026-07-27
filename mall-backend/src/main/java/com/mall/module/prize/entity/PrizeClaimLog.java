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

    /** 奖品类型: COUPON / POINTS / ... */
    private String prizeType;
    /** 奖品关联 ID（优惠券→coupon_template.id, 积分→null） */
    private Long prizeRefId;

    private Date claimTime;
    private Date createTime;
}
