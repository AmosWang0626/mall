package cn.eyeo.mall.adapter.web.common.exception;

import cn.eyeo.mall.adapter.web.common.api.MallOpsResponse;
import com.alibaba.cola.exception.BizException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 统一异常拦截
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2020/12/4
 */
@Slf4j
@RestControllerAdvice
public class AppExceptionAdvice {

    /**
     * Controller统一参数校验
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public MallOpsResponse<String> methodArgumentNotValid(MethodArgumentNotValidException e) {
        ObjectError objectError = e.getBindingResult().getAllErrors().get(0);

        return MallOpsResponse.fail(objectError.getDefaultMessage());
    }

    /**
     * GET POST 等 Method 不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public MallOpsResponse<String> methodNotSupported(HttpServletRequest request,
                                                      HttpRequestMethodNotSupportedException e) {
        log.warn("请求URL无效!!! url: [{}], detail: [{}]", request.getRequestURI(), e.getMessage());
        return MallOpsResponse.fail();
    }

    /**
     * 入参错误
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public MallOpsResponse<String> parameterError(HttpServletRequest request,
                                                  MissingServletRequestParameterException e) {
        log.warn("param error! url: [{}], detail: [{}]", request.getRequestURI(), e.getMessage());
        return MallOpsResponse.fail();
    }

    @ExceptionHandler(BizException.class)
    public MallOpsResponse<String> bizException(HttpServletRequest request, BizException e) {
        log.error("BizException! url: [{}], message: [{}]", request.getRequestURI(), e.getMessage());

        return MallOpsResponse.fail("查无信息");
    }

    /**
     * 默认可作为DAO层异常
     * 用法如下：Preconditions.checkState(update > 0, "系统异常，请稍后重试!");
     */
    @ExceptionHandler(IllegalStateException.class)
    public MallOpsResponse<String> illegalStateException(HttpServletRequest request, IllegalStateException e) {
        log.error("IllegalStateException! url: [{}], message: [{}]", request.getRequestURI(), e.getMessage());

        return MallOpsResponse.fail(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public MallOpsResponse<String> fail(HttpServletRequest request, Exception e) {
        log.error("fail! url: [{}]", request.getRequestURI(), e);

        return MallOpsResponse.fail();
    }

}
