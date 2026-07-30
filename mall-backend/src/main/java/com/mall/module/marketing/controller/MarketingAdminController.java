package com.mall.module.marketing.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.marketing.entity.MarketingActivity;
import com.mall.module.marketing.service.MarketingOpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/marketing")
public class MarketingAdminController {

    @Autowired
    private MarketingOpsService marketingOpsService;

    @GetMapping("/list")
    public Result<PageResult<MarketingActivity>> list(@RequestParam(required = false) String name,
                                                       @RequestParam(required = false) String type,
                                                       @RequestParam(required = false) Integer status,
                                                       @RequestParam(defaultValue = "1") int pageNum,
                                                       @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(marketingOpsService.list(name, type, status, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<MarketingActivity> detail(@PathVariable Long id) {
        return Result.success(marketingOpsService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody MarketingActivity activity) {
        marketingOpsService.save(activity);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        marketingOpsService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam int status) {
        marketingOpsService.updateStatus(id, status);
        return Result.success();
    }
}
