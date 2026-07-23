package com.mall.module.coupon.mapper;

import com.mall.module.coupon.entity.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserCouponMapper {
    int insert(UserCoupon uc);
    int updateById(UserCoupon uc);
    UserCoupon selectById(Long id);
    List<UserCoupon> selectByUserId(@Param("userId") Long userId, @Param("status") Integer status);
    int countByUserAndCoupon(@Param("userId") Long userId, @Param("couponId") Long couponId);
    UserCoupon selectAvailableByUserAndCoupon(@Param("userId") Long userId, @Param("couponId") Long couponId);
    int updateUsed(@Param("id") Long id, @Param("orderId") Long orderId);
    List<UserCoupon> selectByUserIdWithDetail(@Param("userId") Long userId, @Param("status") Integer status);
}
