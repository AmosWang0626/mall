package com.mall.module.prize.spi;

import lombok.Builder;
import lombok.Data;

/**
 * 奖品发放上下文
 * <p>由 {@code PrizePoolService} 在频控通过、库存扣减成功后构建，
 * 传递给 {@link PrizeProvider#grant(PrizeContext)} 执行实际发放。</p>
 */
@Data
@Builder
public class PrizeContext {

    /** 用户 ID */
    private Long userId;

    /** 奖池 ID */
    private Long poolId;

    /** 奖池名称 */
    private String poolName;

    /**
     * 奖品关联 ID
     * <p>优惠券类型 → coupon_template.id；积分类型 → null</p>
     */
    private Long prizeRefId;

    /**
     * 奖品面值
     * <p>积分类型 → 积分数量；优惠券类型 → null（面值从模板查询）</p>
     */
    private Integer prizeValue;

    /** 奖品展示名称 */
    private String prizeName;

    /** 奖品展示描述 */
    private String prizeDesc;
}
