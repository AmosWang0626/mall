package com.mall.module.marketing.controller;

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

    @Autowired
    private MarketingService marketingService;

    @GetMapping("/active")
    public Result<List<MarketingActivity>> active(@RequestParam(required = false) String type) {
        return Result.success(marketingService.activeList(type));
    }

    @GetMapping("/activity/{id}/products")
    public Result<List<ActivityProduct>> products(@PathVariable Long id) {
        return Result.success(marketingService.getActivityProducts(id));
    }
}
