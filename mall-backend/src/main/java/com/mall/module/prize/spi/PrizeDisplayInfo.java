package com.mall.module.prize.spi;

import lombok.Builder;
import lombok.Data;

/**
 * 奖品展示信息
 * <p>由 {@link PrizeProvider#getDisplayInfo(PrizePool)} 返回，
 * 用于奖池列表页展示奖品面值、类型标签等。</p>
 */
@Data
@Builder
public class PrizeDisplayInfo {

    /** 奖品类型（"COUPON" / "POINTS" / ...） */
    private String type;

    /** 类型标签文本（"满减券" / "折扣券" / "无门槛券" / "积分" / ...） */
    private String typeLabel;

    /**
     * 面值展示文本（如 "10.00" / "8.5" / "50"）
     * <p>纯数字部分，前端配合 unit 展示</p>
     */
    private String valueText;

    /** 面单位（"元" / "折" / "积分"） */
    private String valueUnit;

    /** 使用条件文本（如 "满100元可用" / "无门槛" / null） */
    private String conditionText;
}
