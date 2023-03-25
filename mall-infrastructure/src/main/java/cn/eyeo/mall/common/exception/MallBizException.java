package cn.eyeo.mall.common.exception;

import com.alibaba.cola.exception.BizException;
import cn.eyeo.mall.dto.data.ErrorCode;

/**
 * 业务异常
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/11/27
 */
public class MallBizException extends BizException {

    private static final long serialVersionUID = -2776433598172531409L;

    public MallBizException(ErrorCode errorCode) {
        super(errorCode.getErrCode(), errorCode.getErrDesc());
    }
}
