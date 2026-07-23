package com.mall.module.marketing.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.marketing.entity.ActivityProduct;
import com.mall.module.marketing.entity.MarketingActivity;
import com.mall.module.marketing.service.MarketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/marketing")
public class MarketingController {
    @Autowired private MarketingService marketingService;

    // ===== Public =====
    @GetMapping("/active")
    public Result<List<MarketingActivity>> active(@RequestParam(required = false) String type) {
        return Result.success(marketingService.activeList(type));
    }

    @GetMapping("/activity/{id}/products")
    public Result<List<ActivityProduct>> products(@PathVariable Long id) {
        return Result.success(marketingService.getActivityProducts(id));
    }

    // ===== Admin =====
    @GetMapping("/list")
    public Result<PageResult<MarketingActivity>> list(@RequestParam(required = false) String name,
                                                       @RequestParam(required = false) String type,
                                                       @RequestParam(required = false) Integer status,
                                                       @RequestParam(defaultValue = "1") int pageNum,
                                                       @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(marketingService.list(name, type, status, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<MarketingActivity> detail(@PathVariable Long id) { return Result.success(marketingService.getById(id)); }

    @PostMapping
    public Result<Void> save(@RequestBody MarketingActivity activity) { marketingService.save(activity); return Result.success(); }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { marketingService.delete(id); return Result.success(); }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam int status) { marketingService.updateStatus(id, status); return Result.success(); }
}
