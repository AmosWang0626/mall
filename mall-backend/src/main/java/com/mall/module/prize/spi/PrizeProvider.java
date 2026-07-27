package com.mall.module.prize.spi;

import com.mall.module.prize.entity.PrizePool;

/**
 * 奖品发放 SPI 接口
 *
 * <p>每种奖品类型（优惠券、积分、实物等）对应一个实现。
 * 内置实现通过 Spring {@code @Component} 注册，
 * 外部扩展通过 {@code META-INF/services/com.mall.module.prize.spi.PrizeProvider} 注册。</p>
 *
 * <h3>扩展步骤（外部 jar）：</h3>
 * <ol>
 *   <li>实现 {@link PrizeProvider} 接口</li>
 *   <li>在 jar 的 {@code META-INF/services/com.mall.module.prize.spi.PrizeProvider} 文件中写入实现类全限定名</li>
 *   <li>将 jar 加入 classpath 即可自动生效</li>
 * </ol>
 */
public interface PrizeProvider {

    /**
     * 奖品类型标识（如 "COUPON"、"POINTS"）
     * <p>对应 {@code PrizePool.prizeType} 字段</p>
     */
    String getType();

    /**
     * 奖品类型显示名称（如 "优惠券"、"积分"）
     */
    String getDisplayName();

    /**
     * 发放奖品
     * <p>在奖池库存原子扣减成功后、同一线程同一事务内调用。
     * 实现方应确保发放失败时抛出异常以触发事务回滚。</p>
     *
     * @param context 发放上下文（含 userId、poolId、prizeRefId、prizeValue 等）
     * @return 发放结果（含展示信息，用于前端弹窗）
     */
    PrizeResult grant(PrizeContext context);

    /**
     * 获取奖品展示信息（用于列表页展示，非发放时调用）
     * <p>例如优惠券需要查询模板面值/折扣/门槛，积分直接返回 prizeValue。</p>
     *
     * @param pool 奖池信息
     * @return 展示信息
     */
    PrizeDisplayInfo getDisplayInfo(PrizePool pool);
}
