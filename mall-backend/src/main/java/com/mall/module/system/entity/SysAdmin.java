package com.mall.module.system.entity;

import com.mall.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;

/**
 * 系统管理员实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysAdmin extends BaseEntity {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String email;
    private String phone;
    private Integer status;
    private Date lastLogin;
}
