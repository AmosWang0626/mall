package cn.eyeo.mall.client.member.dto;

import com.alibaba.cola.dto.Command;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
public class MemberModifyCmd extends Command {

    @Serial
    private static final long serialVersionUID = 4337056083082492434L;

    /**
     * 用户ID
     */
    @NotNull
    private Long userId;

    /**
     * 用户名
     */
    @NotBlank
    private String username;

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
