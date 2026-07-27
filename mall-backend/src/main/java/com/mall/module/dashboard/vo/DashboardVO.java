package com.mall.module.dashboard.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 仪表盘数据
 */
@Data
public class DashboardVO {

    /** 商品总数 */
    private Long productCount;

    /** 今日订单数 */
    private Long todayOrderCount;

    /** 今日销售额 */
    private BigDecimal todaySales;

    /** 注册用户总数 */
    private Long userCount;

    /** 最近订单 */
    private List<RecentOrder> recentOrders;

    @Data
    public static class RecentOrder {
        private String orderNo;
        private BigDecimal payAmount;
        private Integer status;
    }
}
