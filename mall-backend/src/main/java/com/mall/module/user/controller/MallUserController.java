package com.mall.module.user.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.user.entity.MallUser;
import com.mall.module.user.service.MallUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class MallUserController {

    @Autowired
    private MallUserService userService;

    @GetMapping("/info")
    public Result<MallUser> info() {
        return Result.success(userService.getCurrentUser());
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody MallUser user) {
        userService.updateProfile(user);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody Map<String, String> body) {
        userService.changePassword(body.get("oldPassword"), body.get("newPassword"));
        return Result.success();
    }

    // ===== 后台管理接口 =====
    @GetMapping("/list")
    public Result<PageResult<MallUser>> list(@RequestParam(defaultValue = "") String keyword,
                                              @RequestParam(required = false) Integer status,
                                              @RequestParam(defaultValue = "1") int pageNum,
                                              @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(userService.list(keyword, status, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<MallUser> detail(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @PutMapping
    public Result<Void> update(@RequestBody MallUser user) {
        userService.update(user);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }
}
