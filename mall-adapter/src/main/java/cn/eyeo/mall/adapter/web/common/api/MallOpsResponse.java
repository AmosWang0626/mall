package cn.eyeo.mall.adapter.web.common.api;

import com.alibaba.cola.dto.DTO;
import com.alibaba.cola.dto.Response;
import lombok.Getter;
import lombok.Setter;

/**
 * Mall后台统一Response 适配vben前端框架
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/7/11
 */
@Getter
@Setter
public class MallOpsResponse<T> extends DTO {

    private static final long serialVersionUID = 4872401990676301748L;

    /**
     * 错误码
     * SUCCESS = 0,
     * ERROR = -1,
     * TIMEOUT = 401,
     */
    private Integer code;

    /**
     * 错误描述
     */
    private String message;

    /**
     * 'success' | 'error' | 'warning'
     */
    private String type;

    /**
     * 返回结果
     */
    private T result;

    public static <T> MallOpsResponse<T> success() {
        MallOpsResponse<T> response = new MallOpsResponse<>();
        response.setCode(0);
        response.setMessage("success");
        response.setType("success");
        return response;
    }

    public static <T> MallOpsResponse<T> success(T data) {
        MallOpsResponse<T> response = new MallOpsResponse<>();
        response.setCode(0);
        response.setMessage("成功");
        response.setType("success");
        response.setResult(data);
        return response;
    }

    public static <T> MallOpsResponse<T> fail() {
        MallOpsResponse<T> response = new MallOpsResponse<>();
        response.setCode(-1);
        response.setMessage("系统异常");
        response.setType("error");
        return response;
    }

    public static <T> MallOpsResponse<T> fail(String message) {
        MallOpsResponse<T> response = new MallOpsResponse<>();
        response.setCode(-1);
        response.setMessage(message);
        response.setType("error");
        return response;
    }

    public static <T> MallOpsResponse<T> fail(Response source) {
        MallOpsResponse<T> response = new MallOpsResponse<>();
        response.setCode(-1);
        response.setMessage(String.format("(%s) %s", source.getErrCode(), source.getErrMessage()));
        response.setType("error");
        return response;
    }

    public static <T> MallOpsResponse<T> warn(String message) {
        MallOpsResponse<T> response = new MallOpsResponse<>();
        response.setCode(-1);
        response.setMessage(message);
        response.setType("warning");
        return response;
    }

    public static <T> MallOpsResponse<T> timeout() {
        MallOpsResponse<T> response = new MallOpsResponse<>();
        response.setCode(401);
        response.setMessage("登录超时,请重新登录!");
        response.setType("warning");
        return response;
    }

}
