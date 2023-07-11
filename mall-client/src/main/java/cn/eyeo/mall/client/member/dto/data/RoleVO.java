package cn.eyeo.mall.client.member.dto.data;

import cn.eyeo.mall.client.common.BaseVO;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色信息
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/7/10
 */
@Getter
@Setter
public class RoleVO extends BaseVO {

    private static final long serialVersionUID = 4587055524035841700L;

    /**
     * 角色名字
     */
    private String roleName;

    /**
     * 角色
     */
    private String value;

}
