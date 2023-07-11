package cn.eyeo.mall.adapter.web.common.api;

/**
 * 模块名称: kbase
 * 当前用户上下文
 *
 * @author amos.wang
 * @date 2020/12/8 17:22
 */
public class CurrentUserContext {

    private static final ThreadLocal<Long> CURRENT_USER = new ThreadLocal<>();

    /**
     * 获取当前用户ID
     *
     * @return 当前用户ID
     */
    public static Long get() {
        return CURRENT_USER.get();
    }

    /**
     * 设置当前用户ID
     *
     * @param token 用户ID
     */
    public static void set(Long token) {
        CURRENT_USER.set(token);
    }

    /**
     * 销毁当前用户（避免内存溢出）
     */
    public static void remove() {
        CURRENT_USER.remove();
    }

}
