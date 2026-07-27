package com.mall.module.dashboard.controller;

import com.mall.common.Result;
import com.mall.module.dashboard.service.DashboardService;
import com.mall.module.dashboard.vo.DashboardVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 仪表盘
 */
@Tag(name = "仪表盘")
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Operation(summary = "获取仪表盘统计数据")
    @GetMapping("/stats")
    public Result<DashboardVO> stats() {
        return Result.success(dashboardService.stats());
    }
}
