package com.mall.module.order.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderDTO {
    /** 购物车项ID列表, 为空则下单全部选中商品 */
    private List<Long> cartItemIds;
    private Long addressId;
    private Long couponId;
    private Integer pointsUsed;
    private String remark;
    private Integer payType;
}
