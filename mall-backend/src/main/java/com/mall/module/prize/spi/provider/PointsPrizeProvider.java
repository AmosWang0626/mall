package com.mall.module.prize.spi.provider;

import com.mall.module.points.service.PointsService;
import com.mall.module.prize.entity.PrizePool;
import com.mall.module.prize.spi.PrizeContext;
import com.mall.module.prize.spi.PrizeDisplayInfo;
import com.mall.module.prize.spi.PrizeProvider;
import com.mall.module.prize.spi.PrizeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 积分奖品发放实现
 *
 * <p>奖品类型标识：{@code POINTS}</p>
 * <p>发放方式：调用 {@link PointsService#addPoints} 增加用户积分余额并写入积分流水</p>
 * <p>{@code prizeValue} 对应积分数量，{@code prizeRefId} 为 null（积分无需外部模板）</p>
 */
@Component
public class PointsPrizeProvider implements PrizeProvider {

    public static final String TYPE = "POINTS";

    @Autowired
    private PointsService pointsService;

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getDisplayName() {
        return "积分";
    }

    @Override
    public PrizeResult grant(PrizeContext context) {
        int points = context.getPrizeValue() != null ? context.getPrizeValue() : 0;
        if (points <= 0) {
            throw new IllegalArgumentException("积分奖品面值必须大于0");
        }

        pointsService.addPoints(
                context.getUserId(),
                points,
                "PRIZE_POOL",
                context.getPoolId(),
                "奖池领取: " + context.getPoolName()
        );

        String prizeName = context.getPrizeName() != null ? context.getPrizeName() : points + "积分";

        return PrizeResult.builder()
                .prizeType(TYPE)
                .prizeTypeName(getDisplayName())
                .prizeName(prizeName)
                .displayValue(points + "积分")
                .redirectUrl("/points")
                .remark("积分已到账")
                .build();
    }

    @Override
    public PrizeDisplayInfo getDisplayInfo(PrizePool pool) {
        int points = pool.getPrizeValue() != null ? pool.getPrizeValue() : 0;

        return PrizeDisplayInfo.builder()
                .type(TYPE)
                .typeLabel("积分")
                .valueText(String.valueOf(points))
                .valueUnit("积分")
                .conditionText("可用于积分兑换")
                .build();
    }
}
