package com.mall.module.prize.spi;

import lombok.Builder;
import lombok.Data;

/**
 * 奖品发放结果
 * <p>由 {@link PrizeProvider#grant(PrizeContext)} 返回，
 * 传递给前端展示领取成功弹窗。</p>
 */
@Data
@Builder
public class PrizeResult {

    /** 奖品类型（"COUPON" / "POINTS" / ...） */
    private String prizeType;

    /** 奖品类型显示名称（"优惠券" / "积分" / ...） */
    private String prizeTypeName;

    /** 奖品名称（如"无门槛10元券" / "50积分"） */
    private String prizeName;

    /**
     * 展示面值文本（如 "10.00元" / "8.5折" / "50积分"）
     * <p>前端直接展示此文本，无需自行格式化</p>
     */
    private String displayValue;

    /**
     * 领取成功后前端跳转路径
     * <p>如优惠券 → "/coupons"，积分 → "/points"</p>
     */
    private String redirectUrl;

    /** 附加说明（如"优惠券已放入您的账户" / "积分已到账"） */
    private String remark;
}
