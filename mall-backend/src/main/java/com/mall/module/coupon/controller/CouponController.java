package com.mall.module.coupon.controller;

import com.mall.common.Result;
import com.mall.module.coupon.entity.CouponTemplate;
import com.mall.module.coupon.entity.UserCoupon;
import com.mall.module.coupon.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping("/available")
    public Result<List<CouponTemplate>> available() {
        return Result.success(couponService.available());
    }

    @GetMapping("/mine")
    public Result<List<UserCoupon>> mine(@RequestParam(required = false) Integer status) {
        return Result.success(couponService.myCoupons(status));
    }

    @PostMapping("/receive/{couponId}")
    public Result<Void> receive(@PathVariable Long couponId) {
        couponService.receive(couponId);
        return Result.success();
    }
}
