package com.mall.module.user.controller;

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
}
