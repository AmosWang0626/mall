package com.mall.module.prize.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.prize.entity.PrizePool;
import com.mall.module.prize.service.PrizePoolService;
import com.mall.module.prize.spi.PrizeProvider;
import com.mall.module.prize.spi.PrizeProviderRegistry;
import com.mall.module.prize.spi.PrizeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 奖池控制器
 * <p>
 * 公开接口 (无需登录):
 *   GET /public/prize/banners    — 首页Banner列表
 * <p>
 * 用户接口 (需登录):
 *   GET  /prize/list             — 可领取奖池列表(带用户领取状态 + 奖品展示信息)
 *   POST /prize/claim/{poolId}   — 领取奖品(优惠券/积分/...)
 * <p>
 * 管理接口:
 *   GET    /prize/admin/list     — 分页查询
 *   GET    /prize/admin/{id}     — 详情
 *   POST   /prize/admin          — 新增/更新
 *   DELETE /prize/admin/{id}     — 删除
 *   PUT    /prize/admin/{id}/status — 更新状态
 */
@RestController
public class PrizePoolController {

    @Autowired
    private PrizePoolService prizePoolService;

    @Autowired
    private PrizeProviderRegistry providerRegistry;

    // ===== 公开接口 =====

    @GetMapping("/public/prize/banners")
    public Result<List<PrizePool>> banners() {
        return Result.success(prizePoolService.bannerList());
    }

    // ===== 用户接口 =====

    @GetMapping("/prize/list")
    public Result<List<PrizePool>> list() {
        return Result.success(prizePoolService.activeList());
    }

    @PostMapping("/prize/claim/{poolId}")
    public Result<PrizeResult> claim(@PathVariable Long poolId) {
        return Result.success(prizePoolService.claim(poolId), "领取成功");
    }

    // ===== 管理接口 =====

    /**
     * 已注册的奖品类型列表 (供后台下拉选择, 自动反映 SPI 扩展)
     */
    @GetMapping("/prize/admin/types")
    public Result<List<Map<String, String>>> adminTypes() {
        List<Map<String, String>> types = new ArrayList<>();
        for (PrizeProvider p : providerRegistry.getAllProviders()) {
            Map<String, String> t = new LinkedHashMap<>();
            t.put("type", p.getType());
            t.put("displayName", p.getDisplayName());
            types.add(t);
        }
        return Result.success(types);
    }

    @GetMapping("/prize/admin/list")
    public Result<PageResult<PrizePool>> adminList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(prizePoolService.list(name, status, pageNum, pageSize));
    }

    @GetMapping("/prize/admin/{id}")
    public Result<PrizePool> adminDetail(@PathVariable Long id) {
        return Result.success(prizePoolService.getById(id));
    }

    @PostMapping("/prize/admin")
    public Result<Void> adminSave(@RequestBody PrizePool pool) {
        prizePoolService.save(pool);
        return Result.success();
    }

    @DeleteMapping("/prize/admin/{id}")
    public Result<Void> adminDelete(@PathVariable Long id) {
        prizePoolService.delete(id);
        return Result.success();
    }

    @PutMapping("/prize/admin/{id}/status")
    public Result<Void> adminUpdateStatus(@PathVariable Long id, @RequestParam int status) {
        prizePoolService.updateStatus(id, status);
        return Result.success();
    }
}
