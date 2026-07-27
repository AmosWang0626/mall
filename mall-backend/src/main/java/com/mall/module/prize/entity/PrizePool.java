package com.mall.module.prize.entity;

import com.mall.common.BaseEntity;
import com.mall.module.prize.spi.PrizeDisplayInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 营销奖池
 *
 * <p>通过 {@code prizeType} + {@code prizeRefId} + {@code prizeValue} 三件套
 * 支持任意奖品类型（优惠券、积分等），发放逻辑由对应 {@code PrizeProvider} SPI 实现完成。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PrizePool extends BaseEntity {
    private Long id;
    private String name;
    private String description;

    // ===== 奖品配置（SPI 通用三件套）=====
    /** 奖品类型: COUPON-优惠券, POINTS-积分, ... (对应 PrizeProvider.getType()) */
    private String prizeType;
    /** 奖品关联 ID: 优惠券→coupon_template.id, 积分→null */
    private Long prizeRefId;
    /** 奖品面值: 积分→积分数量, 优惠券→null(从模板查) */
    private Integer prizeValue;
    /** 奖品展示名称（列表页/弹窗用） */
    private String prizeName;
    /** 奖品展示描述 */
    private String prizeDesc;

    // ===== 库存与频控 =====
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
    /** 奖品展示信息（由 PrizeProvider.getDisplayInfo 填充） */
    private transient PrizeDisplayInfo prizeDisplayInfo;
    private Integer userClaimedCount;  // 当前用户已领取次数
    private Integer remainingStock;    // 剩余库存
}
