package cn.eyeo.mall.adapter.web.common.interceptor;

import cn.eyeo.mall.adapter.web.common.api.CurrentUserContext;
import cn.eyeo.mall.adapter.web.common.api.Token;
import cn.eyeo.mall.adapter.web.common.api.MallOpsResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * 用户登录拦截器
 *
 * @author amos.wang
 * @date 2020/12/21 18:28
 */
@Configuration
public class UserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        try {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 如果不需要校验token直接 return true
            if (!needTokenCheck(handlerMethod)) {
                return true;
            }

            if (StringUtils.isNotBlank(authorization)) {
                CurrentUserContext.set(Long.valueOf(authorization));
                return true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        unauthorized(response);

        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        CurrentUserContext.remove();
    }

    /**
     * 是否要检查 TOKEN
     *
     * @param handlerMethod handlerMethod
     * @return 是否需要检查 MEMBER_ID
     */
    private boolean needTokenCheck(HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();
        Token annotation = method.getAnnotation(Token.class);
        return (null == annotation || annotation.check());
    }

    private void unauthorized(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.print(MallOpsResponse.timeout());
        } catch (IOException ignored) {
        }
    }

}
