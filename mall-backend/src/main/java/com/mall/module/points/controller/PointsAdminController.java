package com.mall.module.points.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.points.entity.PointsAccount;
import com.mall.module.points.entity.PointsLog;
import com.mall.module.points.service.PointsOpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/points")
public class PointsAdminController {

    @Autowired
    private PointsOpsService pointsOpsService;

    @GetMapping("/account/{userId}")
    public Result<PointsAccount> account(@PathVariable Long userId) {
        return Result.success(pointsOpsService.getAccount(userId));
    }

    @GetMapping("/logs/{userId}")
    public Result<PageResult<PointsLog>> adminLogs(@PathVariable Long userId,
                                                    @RequestParam(required = false) String source,
                                                    @RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(pointsOpsService.adminLogs(userId, source, pageNum, pageSize));
    }
}
