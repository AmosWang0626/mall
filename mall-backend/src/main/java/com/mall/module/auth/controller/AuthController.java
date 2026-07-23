package com.mall.module.auth.controller;

import com.mall.common.Result;
import com.mall.module.auth.dto.LoginDTO;
import com.mall.module.auth.dto.RegisterDTO;
import com.mall.module.auth.service.AuthService;
import com.mall.module.auth.vo.LoginVO;
import com.mall.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证接口
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 登录 (用户/管理员通用)
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(authService.login(dto));
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<LoginVO> register(@Valid @RequestBody RegisterDTO dto) {
        return Result.success(authService.register(dto));
    }

    /**
     * 获取当前登录信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        UserContext.LoginUser user = UserContext.require();
        Map<String, Object> info = new HashMap<>();
        info.put("userId", user.getUserId());
        info.put("username", user.getUsername());
        info.put("type", user.getType());
        if ("admin".equals(user.getType())) {
            List<String> permissions = authService.getPermissions(user.getUserId());
            info.put("permissions", permissions);
        }
        return Result.success(info);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        UserContext.clear();
        return Result.success();
    }
}
