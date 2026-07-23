package com.mall.module.system.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.system.entity.SysConfig;
import com.mall.module.system.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/system/config")
public class SysConfigController {
    @Autowired private SysConfigService configService;

    @GetMapping("/list")
    public Result<PageResult<SysConfig>> list(@RequestParam(defaultValue="") String keyword,
                                               @RequestParam(defaultValue="1") int pageNum,
                                               @RequestParam(defaultValue="10") int pageSize) {
        return Result.success(configService.list(keyword, pageNum, pageSize));
    }
    @GetMapping("/all")
    public Result<List<SysConfig>> all() { return Result.success(configService.all()); }
    @GetMapping("/key/{key}")
    public Result<String> getByKey(@PathVariable String key) { return Result.success(configService.getValue(key)); }
    @GetMapping("/{id}")
    public Result<SysConfig> detail(@PathVariable Long id) { return Result.success(configService.getById(id)); }
    @PostMapping
    public Result<Void> save(@RequestBody SysConfig config) { configService.save(config); return Result.success(); }
    @PutMapping
    public Result<Void> update(@RequestBody SysConfig config) { configService.save(config); return Result.success(); }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { configService.delete(id); return Result.success(); }
}
