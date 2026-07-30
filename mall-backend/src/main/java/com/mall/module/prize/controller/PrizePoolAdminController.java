package com.mall.module.prize.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.prize.entity.PrizePool;
import com.mall.module.prize.service.PrizePoolOpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class PrizePoolAdminController {

    @Autowired
    private PrizePoolOpsService prizePoolOpsService;

    @GetMapping("/prize/admin/types")
    public Result<List<Map<String, String>>> types() {
        return Result.success(prizePoolOpsService.getPrizeTypes());
    }

    @GetMapping("/prize/admin/list")
    public Result<PageResult<PrizePool>> list(@RequestParam(required = false) String name,
                                               @RequestParam(required = false) Integer status,
                                               @RequestParam(defaultValue = "1") int pageNum,
                                               @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(prizePoolOpsService.list(name, status, pageNum, pageSize));
    }

    @GetMapping("/prize/admin/{id}")
    public Result<PrizePool> detail(@PathVariable Long id) {
        return Result.success(prizePoolOpsService.getById(id));
    }

    @PostMapping("/prize/admin")
    public Result<Void> save(@RequestBody PrizePool pool) {
        prizePoolOpsService.save(pool);
        return Result.success();
    }

    @DeleteMapping("/prize/admin/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        prizePoolOpsService.delete(id);
        return Result.success();
    }

    @PutMapping("/prize/admin/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        prizePoolOpsService.updateStatus(id, status);
        return Result.success();
    }
}
