package com.mall.module.prize.entity;

import com.mall.common.BaseEntity;
import com.mall.module.coupon.entity.CouponTemplate;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 营销奖池
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PrizePool extends BaseEntity {
    private Long id;
    private String name;
    private String description;
    private Long couponId;
    private Integer totalStock;       // -1 = 不限
    private Integer claimedCount;
    private Integer perUserLimit;     // 0 = 不限
    private Integer perUserDailyLimit; // 0 = 不限
    private Integer dailyLimit;       // 0 = 不限
    private Date startTime;
    private Date endTime;
    private Integer status;           // 0-禁用, 1-启用
    private String bannerText;
    private String bannerColor;
    private String bannerColorEnd;
    private Integer sort;

    // ===== transient (仅查询时填充) =====
    private CouponTemplate couponTemplate;
    private Integer userClaimedCount;  // 当前用户已领取次数
    private Integer remainingStock;    // 剩余库存
}
