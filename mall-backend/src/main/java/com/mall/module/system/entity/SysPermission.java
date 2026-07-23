package com.mall.module.system.entity;

import com.mall.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysPermission extends BaseEntity {
    private Long id;
    private Long parentId;
    private String name;
    private String code;
    private Integer type; // 1-menu, 2-button, 3-api
    private String path;
    private String component;
    private String icon;
    private Integer sort;
    private Integer status;
    private Integer visible;
    private java.util.List<SysPermission> children;
}
