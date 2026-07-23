package com.mall.module.order.entity;

import com.mall.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class MallOrder extends BaseEntity {
    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal pointsAmount;
    private BigDecimal payAmount;
    private Integer pointsUsed;
    private Integer pointsEarned;
    private Long couponId;
    private Integer status;
    private Integer payType;
    private Date payTime;
    private Date shipTime;
    private Date receiveTime;
    private Date closeTime;
    private String receiver;
    private String receiverPhone;
    private String receiverAddress;
    private String shipCompany;
    private String shipNo;
    private String remark;
    private Date expireTime;
    // transient
    private List<com.mall.module.order.entity.OrderItem> items;
}
