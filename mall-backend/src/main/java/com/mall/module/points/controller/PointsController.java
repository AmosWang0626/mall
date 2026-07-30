package com.mall.module.points.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.points.entity.PointsAccount;
import com.mall.module.points.entity.PointsLog;
import com.mall.module.points.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/points")
public class PointsController {

    @Autowired
    private PointsService pointsService;

    @GetMapping("/account")
    public Result<PointsAccount> myAccount() {
        return Result.success(pointsService.myAccount());
    }

    @PostMapping("/sign")
    public Result<Void> sign() {
        pointsService.dailySign();
        return Result.success();
    }

    @GetMapping("/logs")
    public Result<PageResult<PointsLog>> myLogs(@RequestParam(required = false) String source,
                                                 @RequestParam(defaultValue = "1") int pageNum,
                                                 @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(pointsService.myLogs(source, pageNum, pageSize));
    }
}
