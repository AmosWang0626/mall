package cn.eyeo.mall.common.exception;

import cn.eyeo.mall.client.common.BaseErrorCode;
import com.alibaba.cola.exception.BizException;

import java.io.Serial;

/**
 * 业务异常
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/11/27
 */
public class MallBizException extends BizException {

    @Serial
    private static final long serialVersionUID = -2776433598172531409L;

    public MallBizException(BaseErrorCode errorCode) {
        super(errorCode.getErrCode(), errorCode.getErrDesc());
    }
}
