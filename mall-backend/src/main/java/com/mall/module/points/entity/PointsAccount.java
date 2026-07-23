package com.mall.module.points.entity;

import lombok.Data;
import java.util.Date;

@Data
public class PointsAccount {
    private Long id;
    private Long userId;
    private Integer balance;
    private Integer frozen;
    private Long totalEarned;
    private Long totalUsed;
    private Integer version;
    private Date createTime;
    private Date updateTime;
}
