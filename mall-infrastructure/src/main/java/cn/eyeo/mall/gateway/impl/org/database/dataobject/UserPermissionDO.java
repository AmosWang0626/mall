package cn.eyeo.mall.gateway.impl.org.database.dataobject;

import cn.eyeo.mall.common.api.BaseDO;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户权限关联表
 *
 * @author amos.wang
 * @date 2023-03-25 20:17
 */
@Getter
@Setter
public class UserPermissionDO extends BaseDO {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 权限ID
     */
    private Long permissionId;
}