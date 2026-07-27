package com.mall.module.dashboard.service;

import com.mall.module.dashboard.vo.DashboardVO;
import com.mall.module.order.entity.MallOrder;
import com.mall.module.order.mapper.MallOrderMapper;
import com.mall.module.product.mapper.ProductMapper;
import com.mall.module.user.mapper.MallUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 仪表盘服务 — 聚合各模块统计数据
 */
@Service
public class DashboardService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private MallOrderMapper orderMapper;

    @Autowired
    private MallUserMapper userMapper;

    public DashboardVO stats() {
        DashboardVO vo = new DashboardVO();

        // 商品总数（未删除）
        vo.setProductCount(productMapper.count(null, null, null, null));

        // 注册用户总数
        vo.setUserCount(userMapper.count(null, null));

        // 今日订单数
        vo.setTodayOrderCount(orderMapper.todayOrderCount());

        // 今日销售额（已付款+已发货+已完成）
        vo.setTodaySales(orderMapper.todaySales());

        // 最近5笔订单
        List<MallOrder> recentOrders = orderMapper.selectRecent(5);
        vo.setRecentOrders(recentOrders.stream().map(o -> {
            DashboardVO.RecentOrder ro = new DashboardVO.RecentOrder();
            ro.setOrderNo(o.getOrderNo());
            ro.setPayAmount(o.getPayAmount());
            ro.setStatus(o.getStatus());
            return ro;
        }).collect(Collectors.toList()));

        return vo;
    }
}
