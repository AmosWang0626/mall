package com.mall.common;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 基础实体类 - 所有实体的父类
 */
@Data
public class BaseEntity implements Serializable {

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    /** 逻辑删除标记: 0-未删除, 1-已删除 */
    private Integer deleted;
}
