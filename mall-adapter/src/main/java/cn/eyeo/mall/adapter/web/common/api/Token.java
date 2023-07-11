package cn.eyeo.mall.adapter.web.common.api;

import java.lang.annotation.*;

/**
 * TOKEN 注解，登录相关使用
 *
 * @author amos
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Token {

    /**
     * 是否检查token，默认为true
     *
     * @return true: 校验; false: 不校验
     */
    boolean check() default true;

}
