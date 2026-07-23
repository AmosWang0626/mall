package com.mall.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.common.Result;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT 认证拦截器
 */
@Slf4j
@Component
public class JwtFilter implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.header}")
    private String header;

    @Value("${jwt.prefix}")
    private String prefix;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 预检放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader(header);
        if (authHeader == null || authHeader.isEmpty()) {
            authHeader = request.getParameter("token");
        }

        if (authHeader == null || authHeader.isEmpty()) {
            return writeError(response, 401, "未提供认证Token");
        }

        String token = jwtUtil.extractToken(authHeader);
        try {
            Claims claims = jwtUtil.parseToken(token);
            UserContext.LoginUser loginUser = new UserContext.LoginUser();
            loginUser.setUserId(claims.get("userId", Long.class));
            loginUser.setUsername(claims.get("username", String.class));
            loginUser.setType(claims.get("type", String.class));
            UserContext.set(loginUser);
            return true;
        } catch (Exception e) {
            log.warn("认证失败: {}", e.getMessage());
            return writeError(response, 401, e.getMessage());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private boolean writeError(HttpServletResponse response, int code, String message) throws Exception {
        response.setStatus(code);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(code, message)));
        return false;
    }
}
