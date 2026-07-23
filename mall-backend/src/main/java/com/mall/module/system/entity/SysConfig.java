package com.mall.module.system.entity;

import lombok.Data;
import java.util.Date;

@Data
public class SysConfig {
    private Long id;
    private String configKey;
    private String configValue;
    private String configType;
    private String name;
    private String description;
    private Integer isSystem;
    private Date createTime;
    private Date updateTime;
}
