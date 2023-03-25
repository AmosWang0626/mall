package cn.eyeo.mall.client.member.dto.data;

import cn.eyeo.mall.client.common.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.time.LocalDate;

/**
 * 用户VO
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/3/25
 */
@Getter
@Setter
public class MemberVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = -6277152970777401441L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
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
