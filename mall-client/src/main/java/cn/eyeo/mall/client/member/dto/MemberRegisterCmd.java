package cn.eyeo.mall.client.member.dto;

import com.alibaba.cola.dto.Command;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serial;
import java.time.LocalDate;

/**
 * 新增用户请求
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/1/8
 */
@Getter
@Setter
public class MemberRegisterCmd extends Command {

    @Serial
    private static final long serialVersionUID = -8452719903711385799L;

    /**
     * 用户名
     */
    @NotBlank
    private String username;

    /**
     * 密码
     */
    @NotBlank
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 性别：0-女，1-男，2-保密
     */
    private Integer gender;

    /**
     * 生日
     */
    private LocalDate birthday;

}
