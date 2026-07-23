package com.mall.module.system.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.system.entity.SysAdmin;
import com.mall.module.system.service.SysAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/admin")
public class SysAdminController {
    @Autowired private SysAdminService adminService;

    @GetMapping("/list")
    public Result<PageResult<SysAdmin>> list(@RequestParam(defaultValue="") String keyword,
                                              @RequestParam(defaultValue="1") int pageNum,
                                              @RequestParam(defaultValue="10") int pageSize) {
        return Result.success(adminService.list(keyword, pageNum, pageSize));
    }
    @GetMapping("/{id}")
    public Result<SysAdmin> detail(@PathVariable Long id) { return Result.success(adminService.getById(id)); }
    @PostMapping
    public Result<Void> save(@RequestBody SysAdmin admin) { adminService.save(admin); return Result.success(); }
    @PutMapping
    public Result<Void> update(@RequestBody SysAdmin admin) { adminService.update(admin); return Result.success(); }
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam int status) { adminService.updateStatus(id, status); return Result.success(); }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { adminService.delete(id); return Result.success(); }
    @PutMapping("/{id}/roles")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody Map<String,List<Long>> body) { adminService.assignRoles(id, body.get("roleIds")); return Result.success(); }
    @GetMapping("/{id}/roles")
    public Result<List<Long>> getRoleIds(@PathVariable Long id) { return Result.success(adminService.getRoleIds(id)); }
    @PutMapping("/{id}/password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String,String> body) { adminService.resetPassword(id, body.get("password")); return Result.success(); }
}
