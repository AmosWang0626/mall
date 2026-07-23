package com.mall.module.system.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.system.entity.SysRole;
import com.mall.module.system.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/role")
public class SysRoleController {
    @Autowired private SysRoleService roleService;

    @GetMapping("/list")
    public Result<PageResult<SysRole>> list(@RequestParam(defaultValue="") String keyword,
                                             @RequestParam(defaultValue="1") int pageNum,
                                             @RequestParam(defaultValue="10") int pageSize) {
        return Result.success(roleService.list(keyword, pageNum, pageSize));
    }
    @GetMapping("/all")
    public Result<List<SysRole>> all() { return Result.success(roleService.all()); }
    @GetMapping("/{id}")
    public Result<SysRole> detail(@PathVariable Long id) { return Result.success(roleService.getById(id)); }
    @PostMapping
    public Result<Void> save(@RequestBody SysRole role) { roleService.save(role); return Result.success(); }
    @PutMapping
    public Result<Void> update(@RequestBody SysRole role) { roleService.save(role); return Result.success(); }
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam int status) { roleService.updateStatus(id, status); return Result.success(); }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { roleService.delete(id); return Result.success(); }
    @PutMapping("/{id}/permissions")
    public Result<Void> assignPermissions(@PathVariable Long id, @RequestBody Map<String,List<Long>> body) { roleService.assignPermissions(id, body.get("permissionIds")); return Result.success(); }
    @GetMapping("/{id}/permissions")
    public Result<List<Long>> getPermissionIds(@PathVariable Long id) { return Result.success(roleService.getPermissionIds(id)); }
}
