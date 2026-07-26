package com.mall.module.prize.service;

import com.mall.common.PageResult;
import com.mall.common.RedisService;
import com.mall.common.exception.BusinessException;
import com.mall.module.coupon.entity.CouponTemplate;
import com.mall.module.coupon.service.CouponService;
import com.mall.module.prize.entity.PrizeClaimLog;
import com.mall.module.prize.entity.PrizePool;
import com.mall.module.prize.mapper.PrizeClaimLogMapper;
import com.mall.module.prize.mapper.PrizePoolMapper;
import com.mall.security.UserContext;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
 * 奖池服务 — 通过奖池发券，控制库存、用户频控
 */
@Service
public class PrizePoolService {

    private static final Logger log = LoggerFactory.getLogger(PrizePoolService.class);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired private PrizePoolMapper poolMapper;
    @Autowired private PrizeClaimLogMapper claimLogMapper;
    @Autowired private CouponService couponService;
    @Autowired private RedisService redisService;

    // ===== 公开接口 =====

    /** Banner 列表 (无需登录) */
    public List<PrizePool> bannerList() {
        return poolMapper.selectBannerList();
    }

    /** 可领取奖池列表 (需登录, 带用户领取状态) */
    public List<PrizePool> activeList() {
        Long userId = UserContext.require().getUserId();
        List<PrizePool> list = poolMapper.selectActiveList();
        for (PrizePool pool : list) {
            // 填充用户已领取次数
            int claimed = claimLogMapper.countByPoolAndUser(pool.getId(), userId);
            pool.setUserClaimedCount(claimed);
            // 计算剩余库存
            if (pool.getTotalStock() == -1) {
                pool.setRemainingStock(-1);
            } else {
                pool.setRemainingStock(pool.getTotalStock() - pool.getClaimedCount());
            }
        }
        return list;
    }

    // ===== 管理端 =====

    public PageResult<PrizePool> list(String name, Integer status, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<PrizePool> info = new PageInfo<>(poolMapper.selectList(name, status));
        return PageResult.of(info.getList(), info.getTotal(), pageNum, pageSize);
    }

    public PrizePool getById(Long id) {
        return poolMapper.selectById(id);
    }

    @Transactional
    public void save(PrizePool pool) {
        if (pool.getId() == null) {
            if (pool.getStatus() == null) pool.setStatus(1);
            if (pool.getClaimedCount() == null) pool.setClaimedCount(0);
            poolMapper.insert(pool);
        } else {
            poolMapper.updateById(pool);
        }
    }

    @Transactional
    public void delete(Long id) {
        poolMapper.deleteById(id);
    }

    @Transactional
    public void updateStatus(Long id, int status) {
        poolMapper.updateStatus(id, status);
    }

    // ===== 核心领券逻辑 =====

    /**
     * 从奖池领取优惠券
     * <p>
     * 频控层次:
     * 1. 时间窗口校验 (DB)
     * 2. 每人限领总数 (DB count)
     * 3. 每人每日限领 (Redis, 当天失效)
     * 4. 每日总量限领 (Redis, 当天失效)
     * 5. 奖池库存原子扣减 (DB UPDATE...WHERE stock > 0)
     * 6. 优惠券库存扣减 + UserCoupon 创建 (CouponService.receive)
     * 7. 领取记录写入 (DB)
     */
    @Transactional
    public CouponTemplate claim(Long poolId) {
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

        // 4. Redis 预检: 每人每日限领
        String userDailyKey = redisKey("user:daily", poolId, userId);
        if (pool.getPerUserDailyLimit() > 0) {
            Long current = redisService.get(userDailyKey, Long.class);
            if (current != null && current >= pool.getPerUserDailyLimit()) {
                throw BusinessException.of("今日领取次数已达上限, 明日再来吧");
            }
        }

        // 5. Redis 预检: 每日总量
        String poolDailyKey = redisKey("pool:daily", poolId, null);
        if (pool.getDailyLimit() > 0) {
            Long current = redisService.get(poolDailyKey, Long.class);
            if (current != null && current >= pool.getDailyLimit()) {
                throw BusinessException.of("今日发券量已达上限, 明日再来吧");
            }
        }

        // 6. 奖池库存原子扣减 (DB)
        int rows = poolMapper.incrementClaimed(poolId);
        if (rows == 0) {
            throw BusinessException.of("手慢了, 优惠券已被抢光");
        }

        // 7. 发券 (CouponService 内部会原子扣减券模板库存 + 创建 UserCoupon)
        //    如果发券失败 (如券模板库存不足), @Transactional 会回滚上面的奖池库存扣减
        couponService.receive(pool.getCouponId());

        // 8. 写领取记录
        PrizeClaimLog logEntry = new PrizeClaimLog();
        logEntry.setPoolId(poolId);
        logEntry.setUserId(userId);
        logEntry.setCouponId(pool.getCouponId());
        logEntry.setClaimTime(now);
        claimLogMapper.insert(logEntry);

        // 9. 更新 Redis 每日计数 (DB 成功后才更新)
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

        log.info("奖池领券成功: poolId={}, userId={}, couponId={}", poolId, userId, pool.getCouponId());

        // 返回优惠券模板信息供前端展示
        CouponTemplate ct = pool.getCouponTemplate();
        if (ct == null) {
            // selectById 不带 JOIN, 单独查一次
            List<PrizePool> active = poolMapper.selectActiveList();
            for (PrizePool p : active) {
                if (p.getId().equals(poolId)) {
                    return p.getCouponTemplate();
                }
            }
        }
        return ct;
    }

    // ===== 工具方法 =====

    private String redisKey(String prefix, Long poolId, Long userId) {
        String today = LocalDate.now().format(DATE_FMT);
        if (userId != null) {
            return "prize:" + prefix + ":" + poolId + ":" + userId + ":" + today;
        }
        return "prize:" + prefix + ":" + poolId + ":" + today;
    }

    /** 设置 key 到当天 23:59:59 过期 */
    private void expireToEndOfDay(String key) {
        long secondsUntilMidnight = 86400 - (System.currentTimeMillis() / 1000) % 86400;
        // 简化: 直接用 1 天 TTL, 误差可接受
        redisService.expire(key, secondsUntilMidnight, TimeUnit.SECONDS);
    }
}
