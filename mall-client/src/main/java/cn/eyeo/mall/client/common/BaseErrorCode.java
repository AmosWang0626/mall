package cn.eyeo.mall.client.common;

/**
 * TODO
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/3/25
 */
public interface BaseErrorCode {

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    String getErrCode();

    /**
     * 获取错误描述
     *
     * @return 错误描述
     */
    String getErrDesc();

    /**
     * 获取详细原因
     *
     * @return 详细原因
     */
    String getErrDetail();

}
