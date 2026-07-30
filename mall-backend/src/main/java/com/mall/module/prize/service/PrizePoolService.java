package com.mall.module.prize.service;

import com.mall.common.RedisService;
import com.mall.common.exception.BusinessException;
import com.mall.module.prize.entity.PrizeClaimLog;
import com.mall.module.prize.entity.PrizePool;
import com.mall.module.prize.mapper.PrizeClaimLogMapper;
import com.mall.module.prize.mapper.PrizePoolMapper;
import com.mall.module.prize.spi.PrizeContext;
import com.mall.module.prize.spi.PrizeDisplayInfo;
import com.mall.module.prize.spi.PrizeProvider;
import com.mall.module.prize.spi.PrizeProviderRegistry;
import com.mall.module.prize.spi.PrizeResult;
import com.mall.security.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 奖池服务 — C端：Banner展示、奖池列表、奖品领取
 */
@Service
public class PrizePoolService {

    private static final Logger log = LoggerFactory.getLogger(PrizePoolService.class);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private PrizePoolMapper poolMapper;
    @Autowired
    private PrizeClaimLogMapper claimLogMapper;
    @Autowired
    private PrizeProviderRegistry providerRegistry;
    @Autowired
    private RedisService redisService;

    /** Banner 列表 (无需登录) */
    public List<PrizePool> bannerList() {
        return poolMapper.selectBannerList();
    }

    /** 可领取奖池列表 (需登录, 带用户领取状态 + 奖品展示信息) */
    public List<PrizePool> activeList() {
        Long userId = UserContext.require().getUserId();
        List<PrizePool> list = poolMapper.selectActiveList();
        for (PrizePool pool : list) {
            int claimed = claimLogMapper.countByPoolAndUser(pool.getId(), userId);
            pool.setUserClaimedCount(claimed);
            if (pool.getTotalStock() == -1) {
                pool.setRemainingStock(-1);
            } else {
                pool.setRemainingStock(pool.getTotalStock() - pool.getClaimedCount());
            }
            fillDisplayInfo(pool);
        }
        return list;
    }

    /**
     * 从奖池领取奖品 (5层频控 + SPI发放)
     */
    @Transactional
    public PrizeResult claim(Long poolId) {
        Long userId = UserContext.require().getUserId();

        // 1. 查奖池
        PrizePool pool = poolMapper.selectById(poolId);
        if (pool == null || pool.getStatus() != 1) {
            throw BusinessException.of("奖池不存在或已停用");
        }

        // 2. 时间窗口
        Date now = new Date();
        if (now.before(pool.getStartTime())) {
            throw BusinessException.of("活动尚未开始");
        }
        if (now.after(pool.getEndTime())) {
            throw BusinessException.of("活动已结束");
        }

        // 3. 每人限领总数
        if (pool.getPerUserLimit() > 0) {
            int userTotal = claimLogMapper.countByPoolAndUser(poolId, userId);
            if (userTotal >= pool.getPerUserLimit()) {
                throw BusinessException.of("您已达到该奖池的领取上限");
            }
        }

        // 4. 每人每日限领 (Redis)
        String userDailyKey = redisKey("user:daily", poolId, userId);
        if (pool.getPerUserDailyLimit() > 0) {
            Long current = redisService.get(userDailyKey, Long.class);
            if (current != null && current >= pool.getPerUserDailyLimit()) {
                throw BusinessException.of("今日领取次数已达上限, 明日再来吧");
            }
        }

        // 5. 每日总量 (Redis)
        String poolDailyKey = redisKey("pool:daily", poolId, null);
        if (pool.getDailyLimit() > 0) {
            Long current = redisService.get(poolDailyKey, Long.class);
            if (current != null && current >= pool.getDailyLimit()) {
                throw BusinessException.of("今日发放量已达上限, 明日再来吧");
            }
        }

        // 6. 库存原子扣减 (DB)
        int rows = poolMapper.incrementClaimed(poolId);
        if (rows == 0) {
            throw BusinessException.of("手慢了, 奖品已被抢光");
        }

        // 7. SPI 发放奖品
        PrizeProvider provider = providerRegistry.get(pool.getPrizeType());
        PrizeContext context = PrizeContext.builder()
                .userId(userId)
                .poolId(poolId)
                .poolName(pool.getName())
                .prizeRefId(pool.getPrizeRefId())
                .prizeValue(pool.getPrizeValue())
                .prizeName(pool.getPrizeName())
                .prizeDesc(pool.getPrizeDesc())
                .build();
        PrizeResult result = provider.grant(context);

        // 8. 领取记录
        PrizeClaimLog logEntry = new PrizeClaimLog();
        logEntry.setPoolId(poolId);
        logEntry.setUserId(userId);
        logEntry.setPrizeType(pool.getPrizeType());
        logEntry.setPrizeRefId(pool.getPrizeRefId());
        logEntry.setClaimTime(now);
        claimLogMapper.insert(logEntry);

        // 9. Redis 每日计数
        if (pool.getPerUserDailyLimit() > 0) {
            Long count = redisService.increment(userDailyKey);
            if (count != null && count == 1) {
                expireToEndOfDay(userDailyKey);
            }
        }
        if (pool.getDailyLimit() > 0) {
            Long count = redisService.increment(poolDailyKey);
            if (count != null && count == 1) {
                expireToEndOfDay(poolDailyKey);
            }
        }

        log.info("奖池领取成功: poolId={}, userId={}, prizeType={}, prizeRefId={}",
                poolId, userId, pool.getPrizeType(), pool.getPrizeRefId());
        return result;
    }

    private void fillDisplayInfo(PrizePool pool) {
        try {
            PrizeProvider provider = providerRegistry.get(pool.getPrizeType());
            PrizeDisplayInfo info = provider.getDisplayInfo(pool);
            pool.setPrizeDisplayInfo(info);
        } catch (Exception e) {
            log.warn("填充奖品展示信息失败: poolId={}, prizeType={}, error={}",
                    pool.getId(), pool.getPrizeType(), e.getMessage());
        }
    }

    private String redisKey(String prefix, Long poolId, Long userId) {
        String today = LocalDate.now().format(DATE_FMT);
        if (userId != null) {
            return "prize:" + prefix + ":" + poolId + ":" + userId + ":" + today;
        }
        return "prize:" + prefix + ":" + poolId + ":" + today;
    }

    private void expireToEndOfDay(String key) {
        long secondsUntilMidnight = 86400 - (System.currentTimeMillis() / 1000) % 86400;
        redisService.expire(key, secondsUntilMidnight, TimeUnit.SECONDS);
    }
}
