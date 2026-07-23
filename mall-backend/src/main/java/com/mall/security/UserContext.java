package com.mall.security;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 当前登录用户上下文 (ThreadLocal)
 */
public class UserContext {

    private static final ThreadLocal<LoginUser> CONTEXT = new ThreadLocal<>();

    public static void set(LoginUser user) {
        CONTEXT.set(user);
    }

    public static LoginUser get() {
        return CONTEXT.get();
    }

    public static LoginUser require() {
        LoginUser user = CONTEXT.get();
        if (user == null) {
            throw new RuntimeException("未登录或登录已过期");
        }
        return user;
    }

    public static void clear() {
        CONTEXT.remove();
    }

    @Data
    @Accessors(chain = true)
    public static class LoginUser {
        private Long userId;
        private String username;
        private String type; // user / admin
    }
}
