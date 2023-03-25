package cn.eyeo.mall.gateway.impl.org.database.dataobject;

import cn.eyeo.mall.common.api.BaseDO;
import lombok.Getter;
import lombok.Setter;

/**
 * 后台角色表
 *
 * @author amos.wang
 * @date 2023-03-25 20:17
 */
@Getter
@Setter
public class RoleDO extends BaseDO {
    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 权限状态：0-禁用，1-正常
     */
    private Boolean status;
}