package com.mall.module.user.entity;

import com.mall.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;

/**
 * 商城用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MallUser extends BaseEntity {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private Integer gender;
    private Integer status;
    private String registerIp;
    private Date lastLogin;
}
