package com.mall.module.user.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.user.entity.MallUser;
import com.mall.module.user.service.MallUserOpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/user")
public class MallUserAdminController {

    @Autowired
    private MallUserOpsService userOpsService;

    @GetMapping("/list")
    public Result<PageResult<MallUser>> list(@RequestParam(defaultValue = "") String keyword,
                                              @RequestParam(required = false) Integer status,
                                              @RequestParam(defaultValue = "1") int pageNum,
                                              @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(userOpsService.list(keyword, status, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<MallUser> detail(@PathVariable Long id) {
        return Result.success(userOpsService.getById(id));
    }

    @PutMapping
    public Result<Void> update(@RequestBody MallUser user) {
        userOpsService.update(user);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userOpsService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userOpsService.delete(id);
        return Result.success();
    }
}
