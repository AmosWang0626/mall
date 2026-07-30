package com.mall.module.coupon.service;

import com.mall.common.exception.BusinessException;
import com.mall.module.coupon.entity.CouponTemplate;
import com.mall.module.coupon.entity.UserCoupon;
import com.mall.module.coupon.mapper.CouponTemplateMapper;
import com.mall.module.coupon.mapper.UserCouponMapper;
import com.mall.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.List;

@Service
public class CouponService {

    @Autowired
    private CouponTemplateMapper templateMapper;
    @Autowired
    private UserCouponMapper userCouponMapper;

    public List<CouponTemplate> available() {
        return templateMapper.selectAvailable();
    }

    public List<UserCoupon> myCoupons(Integer status) {
        return userCouponMapper.selectByUserIdWithDetail(UserContext.require().getUserId(), status);
    }

    @Transactional
    public void receive(Long couponId) {
        Long userId = UserContext.require().getUserId();
        CouponTemplate coupon = templateMapper.selectById(couponId);
        if (coupon == null || coupon.getStatus() != 1) throw BusinessException.of("优惠券不存在或已停用");
        int count = userCouponMapper.countByUserAndCoupon(userId, couponId);
        if (count >= coupon.getPerLimit()) throw BusinessException.of("已超过每人限领数量");
        int rows = templateMapper.incrementIssued(couponId);
        if (rows == 0) throw BusinessException.of("优惠券已被抢光");

        UserCoupon uc = new UserCoupon();
        uc.setUserId(userId);
        uc.setCouponId(couponId);
        uc.setStatus(0);
        if (coupon.getValidType() == 1) {
            uc.setValidStart(coupon.getValidStart());
            uc.setValidEnd(coupon.getValidEnd());
        } else {
            Calendar cal = Calendar.getInstance();
            uc.setValidStart(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, coupon.getValidDays());
            uc.setValidEnd(cal.getTime());
        }
        userCouponMapper.insert(uc);
    }

    public BigDecimal calculateDiscount(Long couponId, Long userId, BigDecimal totalAmount) {
        UserCoupon uc = userCouponMapper.selectAvailableByUserAndCoupon(userId, couponId);
        if (uc == null) return BigDecimal.ZERO;
        CouponTemplate coupon = templateMapper.selectById(couponId);
        if (coupon == null) return BigDecimal.ZERO;
        if (totalAmount.compareTo(coupon.getMinSpend()) < 0) return BigDecimal.ZERO;
        switch (coupon.getType()) {
            case 1:  return coupon.getFaceValue();
            case 2:  return totalAmount.multiply(BigDecimal.ONE.subtract(coupon.getDiscount())).setScale(2, RoundingMode.HALF_UP);
            case 3:  return coupon.getFaceValue();
            default: return BigDecimal.ZERO;
        }
    }

    @Transactional
    public void useCoupon(Long couponId, Long userId, Long orderId) {
        UserCoupon uc = userCouponMapper.selectAvailableByUserAndCoupon(userId, couponId);
        if (uc == null) throw BusinessException.of("优惠券不可用");
        userCouponMapper.updateUsed(uc.getId(), orderId);
    }
}
