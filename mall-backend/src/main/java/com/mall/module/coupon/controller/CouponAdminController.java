package com.mall.module.coupon.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.coupon.entity.CouponTemplate;
import com.mall.module.coupon.service.CouponOpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/coupon")
public class CouponAdminController {

    @Autowired
    private CouponOpsService couponOpsService;

    @GetMapping("/list")
    public Result<PageResult<CouponTemplate>> list(@RequestParam(required = false) String name,
                                                    @RequestParam(required = false) Integer status,
                                                    @RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(couponOpsService.list(name, status, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<CouponTemplate> detail(@PathVariable Long id) {
        return Result.success(couponOpsService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody CouponTemplate coupon) {
        couponOpsService.save(coupon);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        couponOpsService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam int status) {
        couponOpsService.updateStatus(id, status);
        return Result.success();
    }
}
