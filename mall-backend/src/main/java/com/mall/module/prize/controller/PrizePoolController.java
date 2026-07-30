package com.mall.module.prize.controller;

import com.mall.common.Result;
import com.mall.module.prize.entity.PrizePool;
import com.mall.module.prize.service.PrizePoolService;
import com.mall.module.prize.spi.PrizeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PrizePoolController {

    @Autowired
    private PrizePoolService prizePoolService;

    @GetMapping("/public/prize/banners")
    public Result<List<PrizePool>> banners() {
        return Result.success(prizePoolService.bannerList());
    }

    @GetMapping("/prize/list")
    public Result<List<PrizePool>> activeList() {
        return Result.success(prizePoolService.activeList());
    }

    @PostMapping("/prize/claim/{poolId}")
    public Result<PrizeResult> claim(@PathVariable Long poolId) {
        return Result.success(prizePoolService.claim(poolId));
    }
}
