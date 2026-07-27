package com.mall.module.prize.spi.provider;

import com.mall.module.coupon.entity.CouponTemplate;
import com.mall.module.coupon.mapper.CouponTemplateMapper;
import com.mall.module.coupon.service.CouponService;
import com.mall.module.prize.entity.PrizePool;
import com.mall.module.prize.spi.PrizeContext;
import com.mall.module.prize.spi.PrizeDisplayInfo;
import com.mall.module.prize.spi.PrizeProvider;
import com.mall.module.prize.spi.PrizeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 优惠券奖品发放实现
 *
 * <p>奖品类型标识：{@code COUPON}</p>
 * <p>发放方式：调用 {@link CouponService#receive(Long)} 从优惠券模板扣减库存并创建用户优惠券</p>
 * <p>{@code prizeRefId} 对应 {@code coupon_template.id}</p>
 */
@Component
public class CouponPrizeProvider implements PrizeProvider {

    public static final String TYPE = "COUPON";

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponTemplateMapper couponTemplateMapper;

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getDisplayName() {
        return "优惠券";
    }

    @Override
    public PrizeResult grant(PrizeContext context) {
        Long couponId = context.getPrizeRefId();
        // CouponService.receive 内部从 ThreadLocal 获取 userId（与奖池同一请求线程）
        couponService.receive(couponId);

        // 查询优惠券模板构造展示信息
        CouponTemplate ct = couponTemplateMapper.selectById(couponId);
        String displayValue = formatCouponValue(ct);
        String prizeName = ct != null ? ct.getName() : "优惠券";

        return PrizeResult.builder()
                .prizeType(TYPE)
                .prizeTypeName(getDisplayName())
                .prizeName(prizeName)
                .displayValue(displayValue)
                .redirectUrl("/coupons")
                .remark("优惠券已放入您的账户")
                .build();
    }

    @Override
    public PrizeDisplayInfo getDisplayInfo(PrizePool pool) {
        if (pool.getPrizeRefId() == null) {
            return PrizeDisplayInfo.builder()
                    .type(TYPE)
                    .typeLabel("优惠券")
                    .valueText("--")
                    .valueUnit("元")
                    .conditionText(null)
                    .build();
        }

        CouponTemplate ct = couponTemplateMapper.selectById(pool.getPrizeRefId());
        if (ct == null) {
            return PrizeDisplayInfo.builder()
                    .type(TYPE)
                    .typeLabel("优惠券")
                    .valueText("--")
                    .valueUnit("元")
                    .conditionText("券模板不存在")
                    .build();
        }

        String typeLabel = switch (ct.getType()) {
            case 1 -> "满减券";
            case 2 -> "折扣券";
            case 3 -> "无门槛券";
            default -> "优惠券";
        };

        String valueText;
        String valueUnit;
        if (ct.getType() == 2) {
            // 折扣券: discount=0.85 → "8.5" + "折"
            valueText = ct.getDiscount().multiply(BigDecimal.TEN).setScale(1, RoundingMode.HALF_UP).toPlainString();
            valueUnit = "折";
        } else {
            valueText = ct.getFaceValue().setScale(2, RoundingMode.HALF_UP).toPlainString();
            valueUnit = "元";
        }

        String conditionText = switch (ct.getType()) {
            case 1 -> "满" + ct.getMinSpend().setScale(2, RoundingMode.HALF_UP).toPlainString() + "元可用";
            case 2 -> "无门槛, 最多" + ct.getMinSpend().setScale(2, RoundingMode.HALF_UP).toPlainString() + "元";
            default -> "无门槛";
        };

        return PrizeDisplayInfo.builder()
                .type(TYPE)
                .typeLabel(typeLabel)
                .valueText(valueText)
                .valueUnit(valueUnit)
                .conditionText(conditionText)
                .build();
    }

    /** 格式化优惠券面值为展示文本 */
    private String formatCouponValue(CouponTemplate ct) {
        if (ct == null) return "优惠券";
        if (ct.getType() == 2) {
            return ct.getDiscount().multiply(BigDecimal.TEN).setScale(1, RoundingMode.HALF_UP).toPlainString() + "折";
        }
        return ct.getFaceValue().setScale(2, RoundingMode.HALF_UP).toPlainString() + "元";
    }
}
