package com.mall.module.auth.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 登录请求
 */
@Data
public class LoginDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    /** 登录类型: user-普通用户, admin-管理员 */
    private String type = "user";
}
