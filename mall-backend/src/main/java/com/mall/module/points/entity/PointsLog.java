package com.mall.module.points.entity;

import lombok.Data;
import java.util.Date;

@Data
public class PointsLog {
    private Long id;
    private Long userId;
    private String changeType; // EARN, USE, FREEZE, UNFREEZE, REFUND
    private Integer points;
    private Integer balanceAfter;
    private String source; // ORDER, SIGN, COUPON, ADMIN
    private Long refId;
    private String remark;
    private Date createTime;
}
