package com.mall.module.system.controller;

import com.mall.common.Result;
import com.mall.module.system.entity.SysPermission;
import com.mall.module.system.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/system/permission")
public class SysPermissionController {
    @Autowired private SysPermissionService permissionService;

    @GetMapping("/tree")
    public Result<List<SysPermission>> tree() { return Result.success(permissionService.tree()); }
    @GetMapping("/list")
    public Result<List<SysPermission>> list() { return Result.success(permissionService.list()); }
    @GetMapping("/{id}")
    public Result<SysPermission> detail(@PathVariable Long id) { return Result.success(permissionService.getById(id)); }
    @PostMapping
    public Result<Void> save(@RequestBody SysPermission permission) { permissionService.save(permission); return Result.success(); }
    @PutMapping
    public Result<Void> update(@RequestBody SysPermission permission) { permissionService.save(permission); return Result.success(); }
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam int status) { permissionService.updateStatus(id, status); return Result.success(); }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { permissionService.delete(id); return Result.success(); }
}
