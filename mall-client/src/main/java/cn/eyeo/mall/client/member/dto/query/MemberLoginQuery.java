package cn.eyeo.mall.client.member.dto.query;

import com.alibaba.cola.dto.Query;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serial;

/**
 * 用户登录请求
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/1/8
 */
@Getter
@Setter
public class MemberLoginQuery extends Query {

    @Serial
    private static final long serialVersionUID = -715460278838182531L;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

}
