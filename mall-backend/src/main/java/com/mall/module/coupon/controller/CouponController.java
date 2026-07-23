package com.mall.module.coupon.controller;

import com.mall.common.PageResult;
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
    @Autowired private CouponService couponService;

    // ===== User =====
    @GetMapping("/available")
    public Result<List<CouponTemplate>> available() { return Result.success(couponService.available()); }

    @GetMapping("/mine")
    public Result<List<UserCoupon>> mine(@RequestParam(required = false) Integer status) { return Result.success(couponService.myCoupons(status)); }

    @PostMapping("/receive/{couponId}")
    public Result<Void> receive(@PathVariable Long couponId) { couponService.receive(couponId); return Result.success(); }

    // ===== Admin =====
    @GetMapping("/list")
    public Result<PageResult<CouponTemplate>> list(@RequestParam(required = false) String name,
                                                    @RequestParam(required = false) Integer status,
                                                    @RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(couponService.list(name, status, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<CouponTemplate> detail(@PathVariable Long id) { return Result.success(couponService.getById(id)); }

    @PostMapping
    public Result<Void> save(@RequestBody CouponTemplate coupon) { couponService.save(coupon); return Result.success(); }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { couponService.delete(id); return Result.success(); }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam int status) { couponService.updateStatus(id, status); return Result.success(); }
}
